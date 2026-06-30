package com.everrefine.elms.application.dto;

import com.everrefine.elms.domain.model.lesson.LessonWithCourseAndLessonGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/** 全レッスンCSV出力用 DTO */
@Getter
public class LessonWithCourseAndLessonGroupDto {

  @Schema(description = "コースID", example = "1")
  private final Integer courseId;

  @Schema(description = "レッスングループID", example = "2")
  private final Integer lessonGroupId;

  @Schema(description = "レッスンID", example = "3")
  private final Integer lessonId;

  @Schema(description = "コースタイトル", example = "Javaコース")
  private final String courseTitle;

  @Schema(description = "レッスングループタイトル", example = "Java導入")
  private final String lessonGroupTitle;

  @Schema(description = "レッスンタイトル", example = "変数とデータ型")
  private final String lessonTitle;

  @Schema(description = "動画URL", example = "https://example.com/videos/lesson1.mp4")
  private final String videoUrl;

  /**
   * LessonWithCourseAndLessonGroupからDTOを生成する。
   *
   * @param source コース・レッスングループ・レッスンの結合情報
   * @return レッスン詳細DTO
   */
  public static LessonWithCourseAndLessonGroupDto from(LessonWithCourseAndLessonGroup source) {
    return new LessonWithCourseAndLessonGroupDto(
        source.getCourseId(),
        source.getLessonGroupId(),
        source.getLessonId(),
        source.getCourseTitle().getValue(),
        source.getLessonGroupTitle().getValue(),
        source.getLessonTitle().getValue(),
        source.getLessonVideoUrl() != null ? source.getLessonVideoUrl().getValue() : null);
  }

  /**
   * フィールドを指定してインスタンスを生成する。
   *
   * @param courseId コースID
   * @param lessonGroupId レッスングループID
   * @param lessonId レッスンID
   * @param courseTitle コースタイトル
   * @param lessonGroupTitle レッスングループタイトル
   * @param lessonTitle レッスンタイトル
   * @param videoUrl 動画URL
   */
  private LessonWithCourseAndLessonGroupDto(
      Integer courseId,
      Integer lessonGroupId,
      Integer lessonId,
      String courseTitle,
      String lessonGroupTitle,
      String lessonTitle,
      String videoUrl) {
    this.courseId = courseId;
    this.lessonGroupId = lessonGroupId;
    this.lessonId = lessonId;
    this.courseTitle = courseTitle;
    this.lessonGroupTitle = lessonGroupTitle;
    this.lessonTitle = lessonTitle;
    this.videoUrl = videoUrl;
  }
}
