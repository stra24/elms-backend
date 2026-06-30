package com.everrefine.elms.application.dto;

import com.everrefine.elms.domain.model.lesson.LessonGroup;
import com.everrefine.elms.domain.model.lesson.LessonGroupWithLesson;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** レッスングループDTOに関するクラス。 */
public record LessonGroupDto(
    @Schema(description = "レッスングループID", example = "1") Integer id,
    @Schema(description = "コースID", example = "2") Integer courseId,
    @Schema(description = "レッスングループの表示順", example = "1.0") BigDecimal lessonGroupOrder,
    @Schema(description = "レッスングループ名", example = "第1章: 基礎編") String name,
    @Schema(description = "登録日時", example = "2024-01-01T09:00:00") LocalDateTime createdAt,
    @Schema(description = "更新日時", example = "2024-06-01T10:30:00") LocalDateTime updatedAt,
    @Schema(description = "レッスン一覧") List<LessonDto> lessons) {

  /**
   * LessonGroupエンティティからLessonGroupDtoを生成する（レッスン一覧なし）。
   *
   * @param lessonGroup レッスングループエンティティ
   * @return レッスングループDTO
   */
  public static LessonGroupDto from(LessonGroup lessonGroup) {
    return from(lessonGroup, null);
  }

  /**
   * LessonGroupエンティティとレッスンDTOリストからLessonGroupDtoを生成する。
   *
   * @param lessonGroup レッスングループエンティティ
   * @param lessons レッスンDTOリスト
   * @return レッスングループDTO
   */
  public static LessonGroupDto from(LessonGroup lessonGroup, List<LessonDto> lessons) {
    return new LessonGroupDto(
        lessonGroup.getId(),
        lessonGroup.getCourseId(),
        lessonGroup.getLessonGroupOrder().getValue(),
        lessonGroup.getTitle().getValue(),
        lessonGroup.getCreatedAt(),
        lessonGroup.getUpdatedAt(),
        lessons);
  }

  /**
   * LessonGroupWithLessonリストからLessonGroupDtoを生成する。
   *
   * @param lessons レッスングループとレッスンの結合情報リスト
   * @return レッスングループDTO
   */
  public static LessonGroupDto from(List<LessonGroupWithLesson> lessons) {
    LessonGroupWithLesson first = lessons.getFirst();

    List<LessonDto> lessonDtos =
        lessons.stream()
            .filter(lesson -> lesson.getLessonId() != null)
            .map(LessonDto::from)
            .toList();

    return new LessonGroupDto(
        first.getLessonGroupId(),
        first.getCourseId(),
        first.getLessonGroupOrder(),
        first.getLessonGroupTitle(),
        first.getLessonGroupCreatedAt(),
        first.getLessonGroupUpdatedAt(),
        lessonDtos);
  }
}
