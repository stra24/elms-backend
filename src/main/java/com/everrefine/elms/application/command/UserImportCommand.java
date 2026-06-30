package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.user.User;
import com.everrefine.elms.domain.model.user.UserRole;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/** CSV取込用ユーザーのコマンド。CSV全体のユーザー情報を保持する。 */
@Getter
@AllArgsConstructor
public class UserImportCommand {

  private static final String[] EXPECTED_HEADER = {"権限", "氏名", "メールアドレス", "ユーザー名"};

  private final Integer currentUserId;
  private final List<UserImportRowCommand> rows;

  /**
   * CSVファイルを読み込み、取込用Commandに変換する。
   *
   * @param file アップロード対象のCSVファイル
   * @param currentUserId 現在ログイン中のユーザーID
   * @return 取込用Command
   */
  public static UserImportCommand from(MultipartFile file, Integer currentUserId) {
    validateCsvFile(file);
    List<UserImportRowCommand> rows = readRows(file);
    validateDuplicateRows(rows);
    return new UserImportCommand(currentUserId, rows);
  }

  /**
   * 取込対象のユーザー件数を取得する。
   *
   * @return 取込件数
   */
  public int getImportCount() {
    return rows.size();
  }

  /**
   * 各CSV行を保存用ユーザーに変換する。
   *
   * @return 保存用ユーザーリスト
   */
  public List<User> toUsers() {
    return rows.stream().map(UserImportRowCommand::toUser).toList();
  }

  /**
   * 現在ログイン中ユーザーのみ既存IDを維持して、保存用ユーザーリストを作成する。
   *
   * @param currentUser 現在ログイン中ユーザー
   * @return 保存用ユーザーリスト
   */
  public List<User> toUsersKeepingCurrentUserId(User currentUser) {
    return rows.stream()
        .map(
            row ->
                row.toUser(
                    row.hasEmailAddress(currentUser.getEmailAddress())
                        ? currentUser.getId()
                        : null))
        .toList();
  }

  /**
   * CSV内に管理者ユーザーが含まれているか判定する。
   *
   * @return 管理者ユーザーが含まれている場合はtrue
   */
  public boolean containsAdmin() {
    return rows.stream().anyMatch(row -> row.getUserRole() == UserRole.ADMIN);
  }

  /**
   * アップロードされたCSVファイルを検証する。
   *
   * @param file アップロード対象のCSVファイル
   */
  private static void validateCsvFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSVファイルを指定してください");
    }

    String filename = file.getOriginalFilename();
    if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSVファイル形式が不正です");
    }
  }

  /**
   * CSVファイルを読み込み、行Commandのリストに変換する。
   *
   * @param file アップロード対象のCSVファイル
   * @return 行Commandのリスト
   */
  private static List<UserImportRowCommand> readRows(MultipartFile file) {
    List<UserImportRowCommand> rows = new ArrayList<>();

    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
      skipBom(reader);

      String headerLine = reader.readLine();
      if (headerLine == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSVファイルが空です");
      }

      validateCsvHeader(parseCsvLine(headerLine));

      String line;
      int lineNumber = 1;
      while ((line = reader.readLine()) != null) {
        lineNumber++;
        if (line.trim().isEmpty()) {
          continue;
        }

        String[] values = parseCsvLine(line);
        if (values.length != EXPECTED_HEADER.length) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "行" + lineNumber + ": 列数が不正です");
        }

        rows.add(toUserImportRowCommand(values, lineNumber));
      }
    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "CSVファイルの解析に失敗しました: " + e.getMessage());
    }

    return rows;
  }

  /**
   * UTF-8 BOMがある場合はスキップする。
   *
   * @param reader CSV読み込み用Reader
   */
  private static void skipBom(BufferedReader reader) throws java.io.IOException {
    reader.mark(1);
    int bom = reader.read();
    if (bom != 0xFEFF) {
      reader.reset();
    }
  }

  /**
   * CSVヘッダが期待するヘッダと一致するか検証する。
   *
   * @param actualHeader アップロードCSVのヘッダ
   */
  private static void validateCsvHeader(String[] actualHeader) {
    if (actualHeader.length != EXPECTED_HEADER.length) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSVヘッダが不正です");
    }

    for (int i = 0; i < EXPECTED_HEADER.length; i++) {
      if (!EXPECTED_HEADER[i].equals(actualHeader[i])) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSVヘッダが不正です");
      }
    }
  }

  /**
   * CSVの行データを行Commandに変換する。
   *
   * @param values CSVの行データ
   * @param lineNumber CSV上の行番号
   * @return 行Command
   */
  private static UserImportRowCommand toUserImportRowCommand(String[] values, int lineNumber) {
    String roleStr = values[0].trim();
    String realName = values[1].trim();
    String emailAddress = values[2].trim();
    String userName = values[3].trim();

    if (roleStr.isEmpty() || realName.isEmpty() || emailAddress.isEmpty() || userName.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "行" + lineNumber + ": 必須項目が入力されていません");
    }

    UserRole userRole;
    try {
      userRole = UserRole.fromRoleName(roleStr);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "行" + lineNumber + ": 権限が不正です");
    }

    return new UserImportRowCommand(userRole, realName, emailAddress, userName);
  }

  /**
   * CSV内の重複を検証する。
   *
   * @param rows 行Commandのリスト
   */
  private static void validateDuplicateRows(List<UserImportRowCommand> rows) {
    Set<String> emailAddresses = new HashSet<>();
    Set<String> userNames = new HashSet<>();

    for (UserImportRowCommand row : rows) {
      if (!emailAddresses.add(row.getEmailAddress())) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "CSV内に重複するメールアドレスが存在します: " + row.getEmailAddress());
      }
      if (!userNames.add(row.getUserName())) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "CSV内に重複するユーザー名が存在します: " + row.getUserName());
      }
    }
  }

  /**
   * CSVの1行をパースして配列に変換する。ダブルクォートで囲まれたカンマを正しく処理する。
   *
   * @param line CSVの1行
   * @return パースされた値の配列
   */
  private static String[] parseCsvLine(String line) {
    List<String> values = new ArrayList<>();
    StringBuilder currentValue = new StringBuilder();
    boolean inQuotes = false;

    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);

      if (c == '"') {
        if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
          currentValue.append('"');
          i++;
        } else {
          inQuotes = !inQuotes;
        }
      } else if (c == ',' && !inQuotes) {
        values.add(currentValue.toString());
        currentValue = new StringBuilder();
      } else {
        currentValue.append(c);
      }
    }

    values.add(currentValue.toString());
    return values.toArray(new String[0]);
  }
}
