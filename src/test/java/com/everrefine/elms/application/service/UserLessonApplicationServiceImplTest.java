package com.everrefine.elms.application.service;

import static com.everrefine.elms.domain.model.user.Password.encryptAndCreate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.everrefine.elms.application.command.UserLessonCompletionStatusUpdateCommand;
import com.everrefine.elms.application.dto.UserLessonDetailDto;
import com.everrefine.elms.application.dto.UserLessonGroupDto;
import com.everrefine.elms.application.exception.ResourceNotFoundException;
import com.everrefine.elms.domain.model.UserLesson;
import com.everrefine.elms.domain.repository.UserLessonRepository;
import com.everrefine.elms.presentation.request.UserLessonCompletionStatusUpdateRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
@Transactional
public class UserLessonApplicationServiceImplTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

  @Autowired private UserLessonApplicationServiceImpl userLessonApplicationService;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private UserLessonRepository userLessonRepository;

  public Integer createCourse(BigDecimal courseOrder, String title, String description) {
    jdbcTemplate.update(
        """
            INSERT INTO courses (course_order, title, description, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
            """,
        courseOrder,
        title,
        description,
        LocalDateTime.now(),
        LocalDateTime.now());
    return jdbcTemplate.queryForObject(
        "SELECT id FROM courses WHERE title = ?", Integer.class, title);
  }

  public Integer createLessonGroup(Integer courseId, BigDecimal lessonGroupOrder, String title) {
    jdbcTemplate.update(
        """
            INSERT INTO lesson_groups (course_id, lesson_group_order, title, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
            """,
        courseId,
        lessonGroupOrder,
        title,
        LocalDateTime.now(),
        LocalDateTime.now());
    return jdbcTemplate.queryForObject(
        "SELECT id FROM lesson_groups WHERE title = ?", Integer.class, title);
  }

  public Integer createLesson(
      Integer lessonGroupId,
      Integer courseId,
      BigDecimal lessonOrder,
      String title,
      String content,
      String videoUrl) {
    jdbcTemplate.update(
        """
            INSERT INTO lessons (
            lesson_group_id, course_id, lesson_order, title, content, video_url, created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """,
        lessonGroupId,
        courseId,
        lessonOrder,
        title,
        content,
        videoUrl,
        LocalDateTime.now(),
        LocalDateTime.now());
    return jdbcTemplate.queryForObject(
        "SELECT id FROM lessons WHERE title = ?", Integer.class, title);
  }

  public Integer createUser(
      String emailAddress, String password, String realName, String userName, String userRole) {
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
        encryptAndCreate(password).getValue(),
        realName,
        userName,
        null,
        userRole,
        LocalDateTime.now(),
        LocalDateTime.now());
    return jdbcTemplate.queryForObject(
        "SELECT id FROM users WHERE email_address = ?", Integer.class, emailAddress);
  }

  public void createUserLesson(Integer userId, Integer lessonId) {
    jdbcTemplate.update(
        """
            INSERT INTO user_lessons (user_id, lesson_id, created_at, updated_at)
            VALUES (?, ?, ?, ?)
            """,
        userId,
        lessonId,
        LocalDateTime.now(),
        LocalDateTime.now());
  }

  @Test
  void 正常系_レッスン詳細取得で未完了の場合isLessonCompletedがfalseになること() {
    Integer courseId = createCourse(new BigDecimal("1"), "ULテストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "ULテストグループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1"),
            "UL未完了レッスン",
            "説明",
            "https://example.com/video.mp4");
    Integer userId = createUser("ul-not-done@example.com", "p", "太郎", "ulnd", "GENERAL");

    UserLessonDetailDto result =
        userLessonApplicationService.findUserLessonDetail(
            userId, courseId, lessonGroupId, lessonId);

    assertNotNull(result);
    assertEquals(lessonId, result.getId());
    assertFalse(result.isLessonCompleted());
  }

  @Test
  void 正常系_レッスン詳細取得で受講完了済みの場合isLessonCompletedがtrueになること() {
    Integer courseId = createCourse(new BigDecimal("1"), "UL完了コース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "UL完了グループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1"),
            "UL完了レッスン",
            "説明",
            "https://example.com/video.mp4");
    Integer userId = createUser("ul-done@example.com", "p", "次郎", "uld", "GENERAL");
    createUserLesson(userId, lessonId);

    UserLessonDetailDto result =
        userLessonApplicationService.findUserLessonDetail(
            userId, courseId, lessonGroupId, lessonId);

    assertNotNull(result);
    assertEquals(lessonId, result.getId());
    assertTrue(result.isLessonCompleted());
  }

  @Test
  void 異常系_パスのコースまたはグループがレッスンと一致しない場合詳細取得で例外になること() {
    Integer courseId = createCourse(new BigDecimal("1"), "UL整合コース", "説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "UL整合グループ");
    Integer lessonId = createLesson(lessonGroupId, courseId, new BigDecimal("1"), "UL1", "d", null);
    Integer userId = createUser("ul-mismatch@example.com", "p", "三郎", "ulm", "GENERAL");
    Integer otherCourseId = createCourse(new BigDecimal("2"), "UL別コース", "説明");

    assertThrows(
        ResourceNotFoundException.class,
        () ->
            userLessonApplicationService.findUserLessonDetail(
                userId, otherCourseId, lessonGroupId, lessonId));
  }

  @Test
  void 正常系_isLessonCompletedがtrueでレコードが存在するときuserLessonが更新されること() {
    Integer courseId = createCourse(new BigDecimal("1"), "UL更新コース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "UL更新グループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1000"),
            "UL更新レッスン",
            "説明",
            "https://example.com/video.mp4");
    Integer userId = createUser("ul-upd@example.com", "password", "テスト 太郎", "ulupd", "GENERAL");
    createUserLesson(userId, lessonId);

    Timestamp beforeUpdatedAt =
        jdbcTemplate.queryForObject(
            "SELECT updated_at FROM user_lessons WHERE user_id = ? AND lesson_id = ?",
            Timestamp.class,
            userId,
            lessonId);

    UserLessonCompletionStatusUpdateRequest req = new UserLessonCompletionStatusUpdateRequest();
    req.setLessonCompleted(true);
    UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(userId, lessonId);
    userLessonApplicationService.updateUserLesson(courseId, lessonGroupId, cmd);

    Optional<UserLesson> userLessonOpt =
        userLessonRepository.findByUserIdAndLessonId(cmd.getUserId(), cmd.getLessonId());
    assertTrue(userLessonOpt.isPresent());

    Timestamp afterUpdatedAt =
        jdbcTemplate.queryForObject(
            "SELECT updated_at FROM user_lessons WHERE user_id = ? AND lesson_id = ?",
            Timestamp.class,
            userId,
            lessonId);
    assertNotNull(beforeUpdatedAt);
    assertNotNull(afterUpdatedAt);
    assertTrue(afterUpdatedAt.after(beforeUpdatedAt));
  }

  @Test
  void 正常系_isLessonCompletedがtrueでレコードが存在しないときuserLessonが新規作成されること() {
    Integer courseId = createCourse(new BigDecimal("1"), "UL新規コース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "UL新規グループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1000"),
            "UL新規レッスン",
            "説明",
            "https://example.com/video.mp4");
    Integer userId = createUser("ul-new@example.com", "password", "テスト 太郎", "ulnew", "GENERAL");

    UserLessonCompletionStatusUpdateRequest req = new UserLessonCompletionStatusUpdateRequest();
    req.setLessonCompleted(true);
    UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(userId, lessonId);
    assertFalse(userLessonRepository.findByUserIdAndLessonId(userId, lessonId).isPresent());

    userLessonApplicationService.updateUserLesson(courseId, lessonGroupId, cmd);

    assertTrue(userLessonRepository.findByUserIdAndLessonId(userId, lessonId).isPresent());
  }

  @Test
  void 正常系_isLessonCompletedがfalseの時userLessonが削除されること() {
    Integer courseId = createCourse(new BigDecimal("1"), "UL削除コース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "UL削除グループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1000"),
            "UL削除レッスン",
            "説明",
            "https://example.com/video.mp4");
    Integer userId = createUser("ul-del@example.com", "password", "テスト 太郎", "uldel", "GENERAL");
    createUserLesson(userId, lessonId);

    UserLessonCompletionStatusUpdateRequest req = new UserLessonCompletionStatusUpdateRequest();
    req.setLessonCompleted(false);
    UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(userId, lessonId);
    userLessonApplicationService.updateUserLesson(courseId, lessonGroupId, cmd);

    assertFalse(userLessonRepository.findByUserIdAndLessonId(userId, lessonId).isPresent());
  }

  @Test
  void 異常系_Userに存在しないuserIdで受講状態を変更するとエラーになること() {
    Integer courseId = createCourse(new BigDecimal("1"), "ULユーザ無コース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "ULユーザ無グループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1000"),
            "ULユーザ無レッスン",
            "説明",
            "https://example.com/video.mp4");

    UserLessonCompletionStatusUpdateRequest req = new UserLessonCompletionStatusUpdateRequest();
    req.setLessonCompleted(true);
    UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(-999, lessonId);

    assertThrows(
        ResourceNotFoundException.class,
        () -> userLessonApplicationService.updateUserLesson(courseId, lessonGroupId, cmd));
  }

  @Test
  void 異常系_Lessonに存在しないlessonIdで受講状態を変更するとエラーになること() {
    Integer courseId = createCourse(new BigDecimal("1"), "ULレッスン無コース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "ULレッスン無グループ");
    Integer userId = createUser("ul-no-lesson@example.com", "p", "テスト", "uln", "GENERAL");

    UserLessonCompletionStatusUpdateRequest req = new UserLessonCompletionStatusUpdateRequest();
    req.setLessonCompleted(true);
    UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(userId, -999);

    assertThrows(
        ResourceNotFoundException.class,
        () -> userLessonApplicationService.updateUserLesson(courseId, lessonGroupId, cmd));
  }

  @Test
  void 異常系_パスのコースがレッスンと一致しない場合受講状態更新で例外になること() {
    Integer courseId = createCourse(new BigDecimal("1"), "ULPUT整合コース", "説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "ULPUT整合グループ");
    Integer lessonId =
        createLesson(lessonGroupId, courseId, new BigDecimal("1"), "ULPUT1", "d", null);
    Integer userId = createUser("ul-put-mismatch@example.com", "p", "四郎", "ulpm", "GENERAL");
    Integer otherCourseId = createCourse(new BigDecimal("2"), "ULPUT別コース", "説明");

    UserLessonCompletionStatusUpdateRequest req = new UserLessonCompletionStatusUpdateRequest();
    req.setLessonCompleted(true);
    UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(userId, lessonId);

    assertThrows(
        ResourceNotFoundException.class,
        () -> userLessonApplicationService.updateUserLesson(otherCourseId, lessonGroupId, cmd));
  }

  @Test
  void 正常系_該当ユーザーに紐づく該当コースのレッスン一覧を取得できること() {
    // Arrange
    Integer courseId1 = createCourse(new BigDecimal("1"), "はじめ", "コース１");
    Integer courseId2 = createCourse(new BigDecimal("2"), "つぎに", "コース２");
    Integer lessonGroupId1 = createLessonGroup(courseId1, new BigDecimal("1"), "コース１のレッスングループ1");
    Integer lessonGroupId2 = createLessonGroup(courseId1, new BigDecimal("2"), "コース１のレッスングループ2");
    Integer lessonGroupId3 = createLessonGroup(courseId2, new BigDecimal("1"), "コース2のレッスングループ1");
    Integer lessonId1 =
        createLesson(
            lessonGroupId1,
            courseId1,
            new BigDecimal("1000"),
            "UL更新レッスン",
            "コース１のレッスングループ１のレッスン1",
            "https://example.com/video.mp4");
    Integer lessonId2 =
        createLesson(
            lessonGroupId1,
            courseId1,
            new BigDecimal("4000"),
            "UI更新レッスン",
            "コース１のレッスングループ１のレッスン2",
            "https://example.com/video.mp4");
    Integer lessonId3 =
        createLesson(
            lessonGroupId2,
            courseId1,
            new BigDecimal("1000"),
            "Javaレッスン",
            "コース１のレッスングループ2のレッスン1",
            "https://example.com/video.mp4");
    Integer lessonId4 =
        createLesson(
            lessonGroupId3,
            courseId2,
            new BigDecimal("1000"),
            "SQLレッスン",
            "コース2のレッスングループ1のレッスン1",
            "https://example.com/video.mp4");
    Integer userId = createUser("ul-upd@example.com", "password", "テスト 太郎", "ulupd", "GENERAL");
    createUserLesson(userId, lessonId1);
    createUserLesson(userId, lessonId3);
    createUserLesson(userId, lessonId4);
    // Act
    List<UserLessonGroupDto> userLessonGroupDto =
        userLessonApplicationService.findUserLessons(userId, courseId1);
    List<UserLessonGroupDto> userLessonGroupDto2 =
        userLessonApplicationService.findUserLessons(userId, courseId2);
    // Assert
    // コースの順番が正しいか
    assertEquals(courseId1, userLessonGroupDto.getFirst().courseId());
    assertEquals(courseId2, userLessonGroupDto2.getFirst().courseId());
    // 完了状態を判定
    assertTrue(userLessonGroupDto.getFirst().userLessons().getFirst().isLessonCompleted());
    assertFalse(userLessonGroupDto.getFirst().userLessons().getLast().isLessonCompleted());
    // 別コースのレッスンは結果に含まれないこと
    assertFalse(
        userLessonGroupDto.stream()
            .flatMap(g -> g.userLessons().stream())
            .anyMatch(l -> l.lesson().getId().equals(lessonId4)));
    // レッスングループ順になっていること
    assertEquals(lessonGroupId1, userLessonGroupDto.getFirst().id());
    assertEquals(lessonGroupId2, userLessonGroupDto.get(1).id());
    // レッスン順番になっていること
    assertEquals(lessonId1, userLessonGroupDto.getFirst().userLessons().get(0).lesson().getId());
    assertEquals(lessonId2, userLessonGroupDto.getFirst().userLessons().get(1).lesson().getId());
    // 別コースは混ざらないこと
    assertEquals(2, userLessonGroupDto.size());
    assertEquals(lessonGroupId3, userLessonGroupDto2.getFirst().id());
  }

  @Test
  void 異常系_ユーザーが存在しないとき例外になること() {
    // Arrange
    Integer courseId = createCourse(new BigDecimal("1"), "はじめ", "コース１");
    // Act & Assert
    assertThrows(
        ResourceNotFoundException.class,
        () -> userLessonApplicationService.findUserLessons(999, courseId));
  }

  @Test
  void 異常系_コースが存在しないとき例外になること() {
    // Arrange
    Integer userId = createUser("ul-upd@example.com", "password", "テスト 太郎", "ulupd", "GENERAL");
    // Act & Assert
    assertThrows(
        ResourceNotFoundException.class,
        () -> userLessonApplicationService.findUserLessons(userId, 999));
  }
}
