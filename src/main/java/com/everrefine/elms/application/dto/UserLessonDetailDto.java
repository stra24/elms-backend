package com.everrefine.elms.application.dto;

import com.everrefine.elms.domain.model.lesson.Lesson;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

/** ユーザーレッスン詳細。 */
@Getter
public class UserLessonDetailDto extends BaseLessonDto {

  @Schema(description = "レッスン完了フラグ（true: 完了, false: 未完了）", example = "false")
  @JsonProperty("isLessonCompleted")
  private final boolean lessonCompleted;

  private UserLessonDetailDto(
      Integer id,
      Integer lessonGroupId,
      Integer courseId,
      BigDecimal lessonOrder,
      String title,
      String content,
      String videoUrl,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      boolean lessonCompleted) {
    super(id, lessonGroupId, courseId, lessonOrder, title, content, videoUrl, createdAt, updatedAt);
    this.lessonCompleted = lessonCompleted;
  }

  /**
   * LessonエンティティからUserLessonDetailDtoを生成する。
   *
   * @param lesson レッスンエンティティ
   * @param isLessonCompleted レッスン完了フラグ
   * @return ユーザーレッスン詳細DTO
   */
  public static UserLessonDetailDto from(Lesson lesson, boolean isLessonCompleted) {
    return new UserLessonDetailDto(
        lesson.getId(),
        lesson.getLessonGroupId(),
        lesson.getCourseId(),
        lesson.getLessonOrder().getValue(),
        lesson.getTitle().getValue(),
        lesson.getContent() != null ? lesson.getContent().getValue() : null,
        lesson.getVideoUrl() != null ? lesson.getVideoUrl().getValue() : null,
        lesson.getCreatedAt(),
        lesson.getUpdatedAt(),
        isLessonCompleted);
  }
}
