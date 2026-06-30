package com.everrefine.elms.application.dto;

import com.everrefine.elms.domain.model.course.Course;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** コースDTOに関するクラス。 */
@AllArgsConstructor
@Getter
public class CourseDto {

  @Schema(description = "コースID", example = "1")
  private final Integer id;

  @Schema(description = "コースの表示順", example = "1.0")
  private final BigDecimal courseOrder;

  @Schema(description = "サムネイルURL", example = "https://example.com/course-thumbnail.png")
  private final String thumbnailUrl;

  @Schema(description = "コースタイトル", example = "Javaプログラミング入門")
  private final String title;

  @Schema(description = "コース説明", example = "Javaの基礎から応用まで学べるコースです")
  private final String description;

  @Schema(description = "登録日時", example = "2024-01-01T09:00:00")
  private final LocalDateTime createdAt;

  @Schema(description = "更新日時", example = "2024-06-01T10:30:00")
  private final LocalDateTime updatedAt;

  /**
   * CourseエンティティからCourseDtoを生成する。
   *
   * @param course コースエンティティ
   * @return コースDTO
   */
  public static CourseDto from(Course course) {
    return new CourseDto(
        course.getId(),
        course.getCourseOrder().getValue(),
        course.getThumbnailUrl() != null ? course.getThumbnailUrl().getValue() : null,
        course.getTitle().getValue(),
        course.getDescription() != null ? course.getDescription().getValue() : null,
        course.getCreatedAt(),
        course.getUpdatedAt());
  }
}
