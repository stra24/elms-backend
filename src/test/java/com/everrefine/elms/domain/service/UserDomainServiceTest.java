package com.everrefine.elms.domain.service;

import static com.everrefine.elms.domain.model.user.Password.encryptAndCreate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.everrefine.elms.application.command.UserSearchCommand;
import com.everrefine.elms.application.dto.UserPageDto;
import com.everrefine.elms.application.service.UserApplicationServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
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
class UserDomainServiceTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17").withReuse(true);

  @Autowired private UserApplicationServiceImpl userApplicationService;

  @Autowired private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void deleteCourses() {
    jdbcTemplate.execute("DELETE FROM courses");
  }

  // coursesにデータを挿入しcourseIdを取得する
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

  // lessoGroupにデータを挿入しlessonGroupIdを取得する
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

  // Lessonにデータを挿入しlessonIdを取得する
  public Integer createLesson(
      Integer lessonGroupId,
      Integer courseId,
      BigDecimal lessonOrder,
      String title,
      String content,
      String videoRrl) {
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
        videoRrl,
        LocalDateTime.now(),
        LocalDateTime.now());
    return jdbcTemplate.queryForObject(
        "SELECT id FROM lessons WHERE title = ?", Integer.class, title);
  }

  // Userにデータを挿入しuserIdを取得する
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

  // userLessonにデータを挿入する
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
  void 正常系_進捗率を取得できること() {
    // Arrange
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");

    // 3つのレッスンを作成
    Integer lesson1Id =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1000"),
            "レッスン1",
            "説明1",
            "https://example.com/video1.mp4");

    Integer lesson2Id =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("2000"),
            "レッスン2",
            "説明2",
            "https://example.com/video2.mp4");

    Integer lesson3Id =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("3000"),
            "レッスン3",
            "説明3",
            "https://example.com/video3.mp4");

    // Userを作成
    Integer userId = createUser("test@example.com", "password", "テスト 太郎", "testuser", "GENERAL");

    // 2つのUserLessonを作成
    createUserLesson(userId, lesson1Id);
    createUserLesson(userId, lesson2Id);

    int completedLessonCnt = 2;
    int allLessonsCnt = 3;
    BigDecimal progressRate = BigDecimal.valueOf(66.6); // completedLessonCnt / allLessonsCnt * 100

    UserSearchCommand userSearchCommand =
        new UserSearchCommand(1, 10, null, null, null, null, null, null, null);

    // Act
    UserPageDto userPageDto = userApplicationService.findUsers(userSearchCommand);

    // Assert
    assertEquals(progressRate, userPageDto.getUserDtos().getFirst().getProgressRate());
  }

  @Test
  void 正常系_総レッスン数が0のとき進捗率が0になること() {
    // Arrange
    createUser("test@example.com", "password", "テスト 太郎", "testuser", "GENERAL");

    UserSearchCommand userSearchCommand =
        new UserSearchCommand(1, 10, null, null, null, null, null, null, null);

    // Act
    UserPageDto userPageDto = userApplicationService.findUsers(userSearchCommand);

    // Assert
    assertEquals(new BigDecimal("0.0"), userPageDto.getUserDtos().getFirst().getProgressRate());
  }
}
