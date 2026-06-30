package com.everrefine.elms.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.everrefine.elms.application.dto.CourseDto;
import com.everrefine.elms.application.dto.CoursePageDto;
import com.everrefine.elms.application.exception.ResourceNotFoundException;
import com.everrefine.elms.presentation.request.CourseCreateRequest;
import com.everrefine.elms.presentation.request.CourseUpdateRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
public class CourseApplicationServiceTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17").withReuse(true);

  @Autowired private CourseApplicationServiceImpl courseApplicationService;

  @Autowired private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void deleteCourses() {
    jdbcTemplate.execute("DELETE FROM courses");
  }

  // serviceを通して作成する
  private void insertCourse(String title, String description, String thumbnailUrl) {
    CourseCreateRequest request = new CourseCreateRequest();
    request.setTitle(title);
    request.setDescription(description);
    request.setThumbnailUrl(thumbnailUrl);
    courseApplicationService.createCourse(request.toCommand());
  }

  // 最後のidを取得する
  private Integer getLastId() {
    Integer id =
        jdbcTemplate.queryForObject(
            """
            SELECT MAX(id)
            FROM courses
            """,
            Integer.class);
    assertNotNull(id);
    return id;
  }

  @Test
  void 正常系_コースを新規作成できること() {
    // Arrange
    insertCourse("テストタイトル", "テスト説明文", "https://test.com/xxx");
    Integer id = getLastId();

    // Act
    CourseDto dto = courseApplicationService.findCourseById(id);

    // Assert
    assertEquals(id, dto.getId());
    assertEquals("テストタイトル", dto.getTitle());
    assertEquals("テスト説明文", dto.getDescription());
    assertEquals("https://test.com/xxx", dto.getThumbnailUrl());
  }

  @Test
  void 正常系_ページング昇順で取得できること() {
    // Arrange
    insertCourse("Java入門", "a", "https://test.com/xxx");
    insertCourse("Spring解説", "b", "https://test.com/yyy");
    insertCourse("Java実践", "c", "hhttps://test.com/zzz");

    // Act
    CoursePageDto page = courseApplicationService.findCourses(1, 2);

    // Assert
    assertEquals(3, page.getTotalSize());
    assertEquals(1, page.getPageNum());
    assertEquals(2, page.getPageSize());

    var items = page.getCourseDtos();
    assertEquals(2, items.size());
    assertEquals("Java入門", items.get(0).getTitle());
    assertEquals("Spring解説", items.get(1).getTitle());
  }

  @Test
  void 正常系_コースを更新できること() {
    // Arrange
    insertCourse("テストタイトル", "テスト説明文", "https://test.com/xxx");
    Integer id = getLastId();

    Timestamp beforeUpdatedAt =
        jdbcTemplate.queryForObject(
            "SELECT updated_at FROM courses WHERE id = ?", Timestamp.class, id);

    // Act
    CourseUpdateRequest updateRequest = new CourseUpdateRequest();
    updateRequest.setDescription("更新後説明文");
    updateRequest.setTitle("更新後タイトル");
    updateRequest.setThumbnailUrl("https://test.com/xxx");
    updateRequest.setCourseOrder(new BigDecimal("1.0000"));
    courseApplicationService.updateCourse(updateRequest.toCommand(id));

    // Assert
    CourseDto dto = courseApplicationService.findCourseById(id);
    assertEquals(id, dto.getId());
    assertEquals("更新後説明文", dto.getDescription());
    assertEquals("更新後タイトル", dto.getTitle());
    assertEquals("https://test.com/xxx", dto.getThumbnailUrl());
    assertEquals(new BigDecimal("1.0000"), dto.getCourseOrder());

    Integer cnt =
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM courses WHERE id = ?", Integer.class, id);
    assertEquals(1, cnt);

    Timestamp afterUpdatedAt =
        jdbcTemplate.queryForObject(
            "SELECT updated_at FROM courses WHERE id = ?", Timestamp.class, id);
    assertNotNull(beforeUpdatedAt);
    assertNotNull(afterUpdatedAt);
    assertTrue(
        afterUpdatedAt.after(beforeUpdatedAt),
        () ->
            "updated_atが更新前と同一か過去になっています: before=" + beforeUpdatedAt + ", after=" + afterUpdatedAt);
  }

  @Test
  void 正常系_コースを削除できること() {
    // Arrange
    insertCourse("テストタイトル", "テスト説明文", "https://test.com/xxx");
    Integer id = getLastId();

    // Act
    courseApplicationService.deleteCourseById(id);

    // Assert
    assertThrows(
        ResourceNotFoundException.class, () -> courseApplicationService.findCourseById(id));
  }

  @Test
  void 異常系_存在しないIDを更新すると例外になること() {
    // Arrange
    int notExistsId = -9999;

    // Act & Assert
    CourseUpdateRequest updateRequest = new CourseUpdateRequest();
    updateRequest.setDescription("更新後説明文");
    updateRequest.setTitle("更新後タイトル");
    updateRequest.setThumbnailUrl("https://test.com/xxx");
    updateRequest.setCourseOrder(new BigDecimal("1.0000"));
    assertThrows(
        ResourceNotFoundException.class,
        () -> courseApplicationService.updateCourse(updateRequest.toCommand(notExistsId)));
  }
}
