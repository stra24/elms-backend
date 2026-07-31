package com.everrefine.elms.application.service;

import static com.everrefine.elms.domain.model.user.Password.encryptAndCreate;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.everrefine.elms.application.command.PasswordUpdateCommand;
import com.everrefine.elms.application.command.UserImportCommand;
import com.everrefine.elms.application.dto.UserImportResponseDto;
import com.everrefine.elms.domain.model.user.User;
import com.everrefine.elms.domain.repository.UserRepository;
import com.everrefine.elms.presentation.request.PasswordUpdateRequest;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
@Transactional
class UserApplicationServiceTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17").withReuse(true);

  @Autowired private UserApplicationServiceImpl userApplicationService;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private UserRepository userRepository;

  @BeforeEach
  void deleteUsers() {
    jdbcTemplate.execute("DELETE FROM users");
  }

  // userを作成
  public UUID createUser(
      String emailAddress,
      String password,
      String realName,
      String userName,
      String userRole,
      LocalDateTime createdAt) {
    jdbcTemplate.update(
        """
            INSERT INTO users (
                 email_address,
                 password,
                 real_name,
                 user_name,
                 thumbnail_url,
                 user_role,
                 created_at,
                 updated_at
             ) VALUES (?, ?, ?, ?, ?, ?, ?, ?);
            """,
        emailAddress,
        encryptAndCreate(password).value(),
        realName,
        userName,
        null,
        userRole,
        createdAt,
        LocalDateTime.now());
    return jdbcTemplate.queryForObject(
        "SELECT id FROM users WHERE email_address = ?", UUID.class, emailAddress);
  }

  // 認証済みユーザーを作成
  public void createAuthentication(User user) {
    UserDetails userDetails =
        new org.springframework.security.core.userdetails.User(
            user.id().toString(), user.password().value(), List.of());
    Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, List.of());
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  // 最終ログイン履歴を作成
  public void createLastLoginHistory(UUID userId, LocalDateTime dateTime) {
    jdbcTemplate.update(
        "INSERT INTO user_login_histories (user_id, created_at, updated_at) VALUES (?, ?, ?)",
        userId,
        dateTime,
        dateTime);
  }

  @Test
  void 正常系_ユーザー情報がCSV形式で生成されること() throws IOException, CsvValidationException {

    // Arrange
    LocalDateTime dateTime1 = LocalDateTime.of(2026, 3, 21, 9, 30);
    LocalDateTime dateTime2 = LocalDateTime.of(2026, 3, 21, 9, 31);
    UUID userId1 =
        createUser("test1@example.com", "password", "テスト はじめ", "test1\nuser", "GENERAL", dateTime1);
    UUID userId2 =
        createUser(
            "test2@example.com", "password", "テスト\"\" 次郎", "test2,user", "GENERAL", dateTime2);
    createLastLoginHistory(userId1, dateTime1);
    createLastLoginHistory(userId2, dateTime2);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    String dateTimeFormat1 = dateTime1.format(formatter);
    String dateTimeFormat2 = dateTime2.format(formatter);
    // Act
    Resource usersCsv = userApplicationService.exportUsersCsv();

    // Assert
    CSVReader csvReader =
        new CSVReader(new InputStreamReader(usersCsv.getInputStream(), StandardCharsets.UTF_8));

    String[] header = csvReader.readNext();
    if (header[0].startsWith("\uFEFF")) {
      header[0] = header[0].substring(1);
    }

    assertNotNull(header);
    assertArrayEquals(
        new String[] {"ユーザーID", "権限", "氏名", "メールアドレス", "ユーザー名", "作成日時", "最終ログイン日時", "進捗率"}, header);
    String[] row1 = csvReader.readNext();
    assertArrayEquals(
        new String[] {
          String.valueOf(userId1),
          "一般",
          "テスト はじめ",
          "test1@example.com",
          "test1\nuser",
          dateTimeFormat1,
          dateTimeFormat1,
          "0.0%"
        },
        row1);
    String[] row2 = csvReader.readNext();
    assertArrayEquals(
        new String[] {
          String.valueOf(userId2),
          "一般",
          "テスト\"\" 次郎",
          "test2@example.com",
          "test2,user",
          dateTimeFormat2,
          dateTimeFormat2,
          "0.0%"
        },
        row2);
  }

  @Test
  void 正常系_パスワードと更新日時が更新されること() {
    // Arrange
    LocalDateTime dateTime = LocalDateTime.of(2026, 3, 21, 9, 30);
    UUID userId =
        createUser("test@example.com", "currentPass", "テスト はじめ", "tester", "GENERAL", dateTime);
    User user = userRepository.findUserById(userId).orElseThrow();
    createAuthentication(user);

    PasswordUpdateRequest request = new PasswordUpdateRequest("currentPass", "newPass");
    PasswordUpdateCommand passwordUpdateCommand = request.toCommand();
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    // Act
    userApplicationService.updatePassword(passwordUpdateCommand);
    User updatedUser = userRepository.findUserById(userId).orElseThrow();

    // Assert
    assertTrue(passwordEncoder.matches("newPass", updatedUser.password().value()));
    assertNotEquals(updatedUser.updatedAt(), dateTime);
    assertEquals("tester", updatedUser.userName().value());
  }

  @Test
  void 異常系_認証情報がないとき401が返ってくること() {
    // Arrange
    LocalDateTime dateTime = LocalDateTime.of(2026, 3, 21, 9, 30);
    UUID userId =
        createUser("test@example.com", "currentPass", "テスト はじめ", "tester", "GENERAL", dateTime);
    User user = userRepository.findUserById(userId).orElseThrow();
    PasswordUpdateRequest request = new PasswordUpdateRequest("currentPass", "newPass");
    PasswordUpdateCommand passwordUpdateCommand = request.toCommand();
    // Act & Assert
    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class,
            () -> userApplicationService.updatePassword(passwordUpdateCommand));
    assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
  }

  @WithMockUser(username = "00000000-0000-0000-0000-000000000000")
  @Test
  void ユーザーが存在しないとき404が返ってくること() {
    // Arrange
    PasswordUpdateRequest request = new PasswordUpdateRequest("currentPass", "newPass");
    PasswordUpdateCommand passwordUpdateCommand = request.toCommand();

    // Act & Assert
    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class,
            () -> userApplicationService.updatePassword(passwordUpdateCommand));
    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
  }

  @Test
  void 異常系_パスワードが一致しないとき400が返ってくること() {
    // Arrange
    LocalDateTime dateTime = LocalDateTime.of(2026, 3, 21, 9, 30);
    UUID userId =
        createUser("test@example.com", "currentPass", "テスト はじめ", "tester", "GENERAL", dateTime);
    User user = userRepository.findUserById(userId).orElseThrow();
    createAuthentication(user);

    PasswordUpdateRequest request = new PasswordUpdateRequest("wrongPass", "newPass");
    PasswordUpdateCommand passwordUpdateCommand = request.toCommand();

    // Act & Assert
    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class,
            () -> userApplicationService.updatePassword(passwordUpdateCommand));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
  }

  @Test
  void 正常系_CSVファイルをアップロードしてユーザー情報が洗い替えされること() throws IOException {
    // Arrange
    LocalDateTime dateTime = LocalDateTime.of(2026, 3, 21, 9, 30);
    UUID userId1 =
        createUser("test1@example.com", "password1", "テスト 一郎", "test1", "GENERAL", dateTime);
    UUID userId2 =
        createUser("test2@example.com", "password2", "テスト 二郎", "test2", "ADMIN", dateTime);

    String csvContent =
        "\uFEFF"
            + "権限,氏名,メールアドレス,ユーザー名\n"
            + "一般,更新 一郎,test1@example.com,updated1\n"
            + "管理者,更新 二郎,updated2@example.com,updated2\n";

    MultipartFile file =
        new MockMultipartFile(
            "file",
            "users.csv",
            "text/csv",
            new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8)));

    // Act
    UserImportCommand command = UserImportCommand.from(file, userId1);
    UserImportResponseDto response = userApplicationService.importUsersCsv(command);

    // Assert
    assertEquals(2, response.importedCount());

    List<User> users =
        userRepository.findAllByOrderByCreatedAtAscIdAsc().stream()
            .sorted(Comparator.comparing(user -> user.emailAddress().value()))
            .toList();
    assertEquals(2, users.size());
    assertEquals(userId1, users.get(0).id());
    assertTrue(userRepository.findUserById(userId1).isPresent());
    assertEquals("test1@example.com", users.get(0).emailAddress().value());
    assertEquals("更新 一郎", users.get(0).realName().value());
    assertEquals("updated1", users.get(0).userName().value());
    assertEquals("updated2@example.com", users.get(1).emailAddress().value());
    assertEquals("更新 二郎", users.get(1).realName().value());
    assertEquals("updated2", users.get(1).userName().value());

    // パスワードがランダム生成され、既存パスワードが引き継がれていないことを確認
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    assertNotNull(users.get(0).password().value());
    assertNotNull(users.get(1).password().value());
    assertNotEquals(users.get(0).password().value(), users.get(1).password().value());
    assertTrue(!passwordEncoder.matches("password1", users.get(0).password().value()));
    assertTrue(!passwordEncoder.matches("password2", users.get(1).password().value()));
  }

  @Test
  void 異常系_ファイル未指定の場合にエラーになること() {
    // Arrange
    UUID currentUserId = UUID.randomUUID();

    // Act & Assert
    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class, () -> UserImportCommand.from(null, currentUserId));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("CSVファイルを指定してください", ex.getReason());
  }

  @Test
  void 異常系_現在ログイン中ユーザーがCSVに含まれていない場合にエラーになること() throws IOException {
    // Arrange
    LocalDateTime dateTime = LocalDateTime.of(2026, 3, 21, 9, 30);
    UUID userId1 =
        createUser("test1@example.com", "password1", "テスト 一郎", "test1", "GENERAL", dateTime);

    String csvContent =
        "\uFEFF" + "権限,氏名,メールアドレス,ユーザー名\n" + "管理者,新規 ユーザー,new@example.com,newuser\n";

    MultipartFile file =
        new MockMultipartFile(
            "file",
            "users.csv",
            "text/csv",
            new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8)));

    // Act & Assert
    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class,
            () -> userApplicationService.importUsersCsv(UserImportCommand.from(file, userId1)));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("現在ログイン中のユーザーがCSVに含まれていません", ex.getReason());
  }

  @Test
  void 異常系_ADMINユーザーがCSVに含まれていない場合にエラーになること() throws IOException {
    // Arrange
    LocalDateTime dateTime = LocalDateTime.of(2026, 3, 21, 9, 30);
    UUID userId =
        createUser("test@example.com", "password", "テスト 一郎", "test1", "GENERAL", dateTime);

    String csvContent = "\uFEFF" + "権限,氏名,メールアドレス,ユーザー名\n" + "一般,テスト 一郎,test@example.com,test1\n";

    MultipartFile file =
        new MockMultipartFile(
            "file",
            "users.csv",
            "text/csv",
            new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8)));

    // Act & Assert
    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class,
            () -> userApplicationService.importUsersCsv(UserImportCommand.from(file, userId)));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("管理者ユーザーを1人以上含めてください", ex.getReason());
  }

  @Test
  void 異常系_CSVヘッダが不正な場合にエラーになること() throws IOException {
    // Arrange
    UUID currentUserId = UUID.randomUUID();

    String csvContent =
        "\uFEFF" + "不正なヘッダ,氏名,メールアドレス,ユーザー名\n" + "一般,テスト 一郎,test@example.com,test1\n";

    MultipartFile file =
        new MockMultipartFile(
            "file",
            "users.csv",
            "text/csv",
            new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8)));

    // Act & Assert
    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class,
            () ->
                userApplicationService.importUsersCsv(UserImportCommand.from(file, currentUserId)));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("CSVヘッダが不正です", ex.getReason());
  }

  @Test
  void 異常系_CSVファイル形式が不正な場合にエラーになること() throws IOException {
    // Arrange
    UUID currentUserId = UUID.randomUUID();

    String csvContent = "test content";
    MultipartFile file =
        new MockMultipartFile(
            "file",
            "users.txt",
            "text/plain",
            new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8)));

    // Act & Assert
    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class,
            () ->
                userApplicationService.importUsersCsv(UserImportCommand.from(file, currentUserId)));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("CSVファイル形式が不正です", ex.getReason());
  }
}
