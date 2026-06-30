package com.everrefine.elms.application.service;

import com.everrefine.elms.application.command.LoginHistoryCreateCommand;
import com.everrefine.elms.application.command.PasswordUpdateCommand;
import com.everrefine.elms.application.command.UserCreateCommand;
import com.everrefine.elms.application.command.UserImportCommand;
import com.everrefine.elms.application.command.UserSearchCommand;
import com.everrefine.elms.application.command.UserUpdateCommand;
import com.everrefine.elms.application.dto.UserDto;
import com.everrefine.elms.application.dto.UserImportResponseDto;
import com.everrefine.elms.application.dto.UserPageDto;
import com.everrefine.elms.application.exception.ResourceNotFoundException;
import com.everrefine.elms.domain.model.user.EmailAddress;
import com.everrefine.elms.domain.model.user.ProgressRate;
import com.everrefine.elms.domain.model.user.User;
import com.everrefine.elms.domain.model.user.UserLoginHistory;
import com.everrefine.elms.domain.repository.LessonRepository;
import com.everrefine.elms.domain.repository.UserLessonRepository;
import com.everrefine.elms.domain.repository.UserLoginHistoryRepository;
import com.everrefine.elms.domain.repository.UserRepository;
import com.everrefine.elms.domain.service.UserDomainService;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** ユーザーアプリケーションサービスの実装に関するクラス。 */
@Service
@AllArgsConstructor
public class UserApplicationServiceImpl implements UserApplicationService {

  private final UserRepository userRepository;
  private final UserLoginHistoryRepository userLoginHistoryRepository;
  private final UserLessonRepository userLessonRepository;
  private final LessonRepository lessonRepository;
  private final UserDomainService userDomainService;

  /**
   * CSV出力用に値をエスケープする。
   *
   * <p>値にカンマ、ダブルクォーテーション、改行があった場合のエスケープ処理。
   *
   * @param value エスケープ対象の文字列
   * @return エスケープ後の文字列
   */
  private String escape(String value) {
    if (value == null) {
      return "";
    }
    if (value.contains(",")
        || value.contains("\"")
        || value.contains("\n")
        || value.contains("\r")) {
      return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    return value;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto findUserById(Integer userId) {
    User user =
        userRepository
            .findUserById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(User.class, String.valueOf(userId)));
    UserLoginHistory userLoginHistory =
        userLoginHistoryRepository.findUserLoginHistoryByUserId(user.getId()).orElse(null);
    int completedLessonCnt = userLessonRepository.countAllByUserId(user.getId());
    int allLessonsCnt = lessonRepository.countAllLessons();
    ProgressRate progressRate = ProgressRate.of(completedLessonCnt, allLessonsCnt);
    return UserDto.from(user, userLoginHistory, progressRate.toBigDecimal());
  }

  @Override
  @Transactional(readOnly = true)
  public UserPageDto findUsers(UserSearchCommand userSearchCommand) {
    var userSearchCondition = userSearchCommand.toSearchCondition();
    List<Integer> userIds = userRepository.findUserIdsBySearchConditions(userSearchCondition);
    List<User> users = userRepository.findUsersByIds(userIds);

    if (users.isEmpty()) {
      int totalSize = userRepository.countUsers(userSearchCondition);
      return UserPageDto.from(
          List.of(),
          userSearchCondition.getPagerForRequest().getPageNum(),
          userSearchCondition.getPagerForRequest().getPageSize(),
          totalSize);
    }

    // ユーザーIDと、そのユーザーのログイン履歴のMap
    Map<Integer, UserLoginHistory> loginHistoryMap =
        userLoginHistoryRepository.findByUserIdsAsMap(userIds);
    Map<Integer, Integer> completedLessonCounts = userLessonRepository.countByUserIds(userIds);
    int allLessonsCnt = lessonRepository.countAllLessons();

    List<UserDto> userDtos =
        users.stream()
            .map(
                user -> {
                  UserLoginHistory userLoginHistory = loginHistoryMap.get(user.getId());
                  int completedLessonCnt = completedLessonCounts.getOrDefault(user.getId(), 0);
                  ProgressRate progressRate = ProgressRate.of(completedLessonCnt, allLessonsCnt);
                  return UserDto.from(user, userLoginHistory, progressRate.toBigDecimal());
                })
            .toList();

    int totalSize = userRepository.countUsers(userSearchCondition);
    return UserPageDto.from(
        userDtos,
        userSearchCondition.getPagerForRequest().getPageNum(),
        userSearchCondition.getPagerForRequest().getPageSize(),
        totalSize);
  }

  @Override
  @Transactional
  public void createUser(UserCreateCommand userCreateCommand) {
    if (!UserDomainService.matchesPassword(
        userCreateCommand.getPassword(), userCreateCommand.getConfirmPassword())) {
      throw new IllegalArgumentException("パスワードの確認が一致しません");
    }

    userRepository.createUser(userCreateCommand.toUser());
  }

  @Override
  @Transactional
  public void updateUser(UserUpdateCommand userUpdateCommand) {
    User user =
        userRepository
            .findUserById(userUpdateCommand.getId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        User.class, String.valueOf(userUpdateCommand.getId())));
    userRepository.updateUser(userUpdateCommand.toUser(user));
  }

  @Override
  @Transactional
  public void deleteUserById(Integer userId) {
    // ユーザーが存在しなくてもエラーにはしない。
    userRepository.findUserById(userId).ifPresent(user -> userRepository.deleteUserById(userId));
  }

  @Transactional
  @Override
  public void updateUserLoginHistory(LoginHistoryCreateCommand loginHistoryCreateCommand) {
    User user =
        userRepository
            .findUserByEmailAddress(new EmailAddress(loginHistoryCreateCommand.getEmail()))
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    Optional<UserLoginHistory> userLoginHistory =
        userLoginHistoryRepository.findUserLoginHistoryByUserId(user.getId());

    // 該当ユーザーのログイン履歴がすでにあれば更新、なければ作成
    userLoginHistory.ifPresentOrElse(
        history -> {
          userLoginHistoryRepository.save(history.update());
        },
        () -> {
          userLoginHistoryRepository.save(UserLoginHistory.create(user.getId()));
        });
  }

  @Transactional(readOnly = true)
  @Override
  public Resource exportUsersCsv() {

    String[] header = {"ユーザーID", "権限", "氏名", "メールアドレス", "ユーザー名", "作成日時", "最終ログイン日時", "進捗率"};

    List<User> users = userRepository.findAllByOrderByIdAsc();

    if (users.isEmpty()) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try (PrintWriter file =
          new PrintWriter(
              new BufferedWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8)))) {
        file.print('\uFEFF');
        file.println(String.join(",", header));
      } catch (Exception e) {
        throw new RuntimeException("CSVファイルの作成に失敗しました", e);
      }
      return new ByteArrayResource(baos.toByteArray());
    }

    // ユーザーIDと、そのユーザーのログイン履歴のMap
    List<Integer> userIds = users.stream().map(User::getId).toList();
    Map<Integer, UserLoginHistory> loginHistoryMap =
        userLoginHistoryRepository.findByUserIdsAsMap(userIds);
    Map<Integer, Integer> completedLessonCounts = userLessonRepository.countByUserIds(userIds);
    int allLessonsCnt = lessonRepository.countAllLessons();

    // 最終ログイン日時と進捗率をusersに追加する
    List<UserDto> userDtos =
        users.stream()
            .map(
                user -> {
                  UserLoginHistory userLoginHistory = loginHistoryMap.get(user.getId());
                  int completedLessonCnt = completedLessonCounts.getOrDefault(user.getId(), 0);
                  ProgressRate progressRate = ProgressRate.of(completedLessonCnt, allLessonsCnt);
                  return UserDto.from(user, userLoginHistory, progressRate.toBigDecimal());
                })
            .toList();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try (
    // 書き込むファイルを作成
    PrintWriter file =
        new PrintWriter(
            new BufferedWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))); ) {
      // Excelで開いたときの文字化けを防ぐ
      file.print('\uFEFF');

      // ヘッダーをセットする
      file.println(String.join(",", header));

      // 中身をセットする ( 1ユーザーごとに改行 )
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
      for (UserDto userDto : userDtos) {
        String[] user = {
          escape(String.valueOf(userDto.getId())),
          escape(userDto.getUserRole()),
          escape(userDto.getRealName()),
          escape(userDto.getEmailAddress()),
          escape(userDto.getUserName()),
          escape(userDto.getCreatedAt().format(formatter)),
          userDto.getLastLoginAt() == null
              ? ""
              : escape(userDto.getLastLoginAt().format(formatter)),
          escape(userDto.getProgressRate().toString() + "%")
        };
        file.println(String.join(",", user));
      }
    } catch (Exception e) {
      throw new RuntimeException("CSVファイルの作成に失敗しました", e);
    }
    return new ByteArrayResource(baos.toByteArray());
  }

  @Transactional
  @Override
  public void updatePassword(PasswordUpdateCommand passwordUpdateCommand) {
    User user = userDomainService.getLoginUser();

    if (!user.isCurrentPasswordMatch(passwordUpdateCommand.getCurrentPassword())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    userRepository.updateUser(
        passwordUpdateCommand.toUser(user, passwordUpdateCommand.getNewPassword()));
  }

  /**
   * CSVファイルをアップロードしてユーザー情報を一括更新する。CSVの内容でユーザーを洗い替え（DELETE → INSERT）する。
   *
   * @param userImportCommand ユーザー取込用Command
   * @return 取込した件数
   */
  @Override
  @Transactional
  public UserImportResponseDto importUsersCsv(UserImportCommand userImportCommand) {
    User currentUser = findCurrentUser(userImportCommand);
    validateCurrentUserIncluded(userImportCommand, currentUser);
    validateAdminIncluded(userImportCommand);
    List<User> users = userImportCommand.toUsersKeepingCurrentUserId(currentUser);

    userRepository.deleteAllUsers();
    userRepository.saveAllUsers(users);

    return new UserImportResponseDto(userImportCommand.getImportCount());
  }

  /**
   * 現在ログイン中のユーザーを取得する。
   *
   * @param userImportCommand ユーザー取込用Command
   * @return 現在ログイン中のユーザー
   */
  private User findCurrentUser(UserImportCommand userImportCommand) {
    return userRepository
        .findUserById(userImportCommand.getCurrentUserId())
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    User.class, String.valueOf(userImportCommand.getCurrentUserId())));
  }

  /**
   * 現在ログイン中のユーザーがCSVに含まれているか検証する。
   *
   * @param userImportCommand ユーザー取込用Command
   * @param currentUser 現在ログイン中のユーザー
   */
  private void validateCurrentUserIncluded(UserImportCommand userImportCommand, User currentUser) {
    boolean currentUserExists =
        userImportCommand.getRows().stream()
            .anyMatch(row -> row.hasEmailAddress(currentUser.getEmailAddress()));
    if (!currentUserExists) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "現在ログイン中のユーザーがCSVに含まれていません");
    }
  }

  /**
   * CSVに管理者ユーザーが含まれているか検証する。
   *
   * @param userImportCommand ユーザー取込用Command
   */
  private void validateAdminIncluded(UserImportCommand userImportCommand) {
    if (!userImportCommand.containsAdmin()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "管理者ユーザーを1人以上含めてください");
    }
  }
}
