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
import java.util.UUID;
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
    CourseCreateRequest request = new CourseCreateRequest(title, description, thumbnailUrl);
    courseApplicationService.createCourse(request.toCommand());
  }

  // タイトルからidを取得する
  private UUID getIdByTitle(String title) {
    UUID id =
        jdbcTemplate.queryForObject("SELECT id FROM courses WHERE title = ?", UUID.class, title);
    assertNotNull(id);
    return id;
  }

  @Test
  void 正常系_コースを新規作成できること() {
    // Arrange
    insertCourse("テストタイトル", "テスト説明文", "https://test.com/xxx");
    UUID id = getIdByTitle("テストタイトル");

    // Act
    CourseDto dto = courseApplicationService.findCourseById(id);

    // Assert
    assertEquals(id, dto.id());
    assertEquals("テストタイトル", dto.title());
    assertEquals("テスト説明文", dto.description());
    assertEquals("https://test.com/xxx", dto.thumbnailUrl());
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
    assertEquals(3, page.totalSize());
    assertEquals(1, page.pageNum());
    assertEquals(2, page.pageSize());

    var items = page.courseDtos();
    assertEquals(2, items.size());
    assertEquals("Java入門", items.get(0).title());
    assertEquals("Spring解説", items.get(1).title());
  }

  @Test
  void 正常系_コースを更新できること() {
    // Arrange
    insertCourse("テストタイトル", "テスト説明文", "https://test.com/xxx");
    UUID id = getIdByTitle("テストタイトル");

    Timestamp beforeUpdatedAt =
        jdbcTemplate.queryForObject(
            "SELECT updated_at FROM courses WHERE id = ?", Timestamp.class, id);

    // Act
    CourseUpdateRequest updateRequest =
        new CourseUpdateRequest(
            null, new BigDecimal("1.0000"), "更新後タイトル", "更新後説明文", "https://test.com/xxx");
    courseApplicationService.updateCourse(updateRequest.toCommand(id));

    // Assert
    CourseDto dto = courseApplicationService.findCourseById(id);
    assertEquals(id, dto.id());
    assertEquals("更新後説明文", dto.description());
    assertEquals("更新後タイトル", dto.title());
    assertEquals("https://test.com/xxx", dto.thumbnailUrl());
    assertEquals(new BigDecimal("1.0000"), dto.courseOrder());

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
    UUID id = getIdByTitle("テストタイトル");

    // Act
    courseApplicationService.deleteCourseById(id);

    // Assert
    assertThrows(
        ResourceNotFoundException.class, () -> courseApplicationService.findCourseById(id));
  }

  @Test
  void 異常系_存在しないIDを更新すると例外になること() {
    // Arrange
    UUID notExistsId = UUID.randomUUID();

    // Act & Assert
    CourseUpdateRequest updateRequest =
        new CourseUpdateRequest(
            null, new BigDecimal("1.0000"), "更新後タイトル", "更新後説明文", "https://test.com/xxx");
    assertThrows(
        ResourceNotFoundException.class,
        () -> courseApplicationService.updateCourse(updateRequest.toCommand(notExistsId)));
  }
}
