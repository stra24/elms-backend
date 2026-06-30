package com.everrefine.elms.application.service;

import static com.everrefine.elms.domain.model.user.Password.encryptAndCreate;
import static org.junit.jupiter.api.Assertions.*;

import com.everrefine.elms.application.dto.UserCourseDto;
import com.everrefine.elms.application.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
public class UserCourseApplicationServiceImplTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

  @Autowired private UserCourseApplicationService userCourseApplicationService;

  @Autowired private JdbcTemplate jdbcTemplate;

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
              lesson_group_id,
              course_id,
              lesson_order,
              title,
              content,
              video_url,
              created_at,
              updated_at
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
  void 正常系_進捗付きのコース一覧が返ること() {
    // 1. usersテーブルに1ユーザー作る
    Integer userId = createUser("ul-not-done@example.com", "p", "太郎", "ulnd", "GENERAL");
    // 2. coursesテーブルに2コース作る
    int courseId1 = createCourse(new BigDecimal("1001"), "ULテストコース", "コース説明");
    int courseId2 = createCourse(new BigDecimal("1002"), "ULテストコース2", "コース説明2");
    int lessonGroupId1 = createLessonGroup(courseId1, new BigDecimal(1), "ULテストコースのグループ");
    int lessonGroupId2 = createLessonGroup(courseId2, new BigDecimal(2), "ULテストコース2のグループ");

    // 3. lessonsテーブルレッスンを3個（1個と2個）作る
    int completedLessonId1 =
        createLesson(
            lessonGroupId1,
            courseId1,
            new BigDecimal("1"),
            "UL完了レッスン1",
            "説明",
            "https://example.com/video.mp4");

    int completedLessonId2 =
        createLesson(
            lessonGroupId2,
            courseId2,
            new BigDecimal("1"),
            "UL完了レッスン2",
            "説明",
            "https://example.com/video.mp4");

    createLesson(
        lessonGroupId2,
        courseId2,
        new BigDecimal("2"),
        "UL未完了レッスン1",
        "説明",
        "https://example.com/video.mp4");
    // 4. userLessonsテーブルに2個（1個と1個）作る
    createUserLesson(userId, completedLessonId1);
    createUserLesson(userId, completedLessonId2);

    List<UserCourseDto> result = userCourseApplicationService.findUserCourses(userId);
    assertNotNull(result);
    UserCourseDto course1 =
        result.stream().filter(dto -> dto.getTitle().equals("ULテストコース")).findFirst().orElseThrow();
    UserCourseDto course2 =
        result.stream().filter(dto -> dto.getTitle().equals("ULテストコース2")).findFirst().orElseThrow();
    assertEquals(courseId1, course1.getId());
    assertEquals(new BigDecimal("100.0"), course1.getCourseProgress());
    assertEquals(courseId2, course2.getId());
    assertEquals(new BigDecimal("50.0"), course2.getCourseProgress());
  }

  @Test
  void 異常系_存在しないuserIdを指定した場合は例外になること() {
    assertThrows(
        ResourceNotFoundException.class, () -> userCourseApplicationService.findUserCourses(-999));
  }

  @Test
  void 正常系_コースが0件の場合空リストを返すこと() {
    jdbcTemplate.execute("DELETE FROM courses");

    Integer userId = createUser("ul-put-mismatch@example.com", "p", "四郎", "ulpm", "GENERAL");
    List<UserCourseDto> result = userCourseApplicationService.findUserCourses(userId);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}
