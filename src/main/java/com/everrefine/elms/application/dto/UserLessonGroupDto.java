package com.everrefine.elms.application.dto;

import com.everrefine.elms.domain.model.lesson.LessonGroupWithLesson;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** ユーザーレッスングループDTOに関するクラス。 */
public record UserLessonGroupDto(
    @Schema(description = "レッスングループID", example = "1") Integer id,
    @Schema(description = "コースID", example = "2") Integer courseId,
    @Schema(description = "レッスングループの表示順", example = "1.0") BigDecimal lessonGroupOrder,
    @Schema(description = "レッスングループ名", example = "第1章: 基礎編") String name,
    @Schema(description = "登録日時", example = "2024-01-01T09:00:00") LocalDateTime createdAt,
    @Schema(description = "更新日時", example = "2024-06-01T10:30:00") LocalDateTime updatedAt,
    @Schema(description = "レッスン一覧") List<UserLessonDto> userLessons) {

  /**
   * LessonGroupWithLessonリストとUserLessonDtoリストからUserLessonGroupDtoを生成する。
   *
   * @param lessons レッスングループとレッスンの結合情報リスト
   * @param userLessonDtos ユーザーレッスンDTOリスト
   * @return ユーザーレッスングループDTO
   */
  public static UserLessonGroupDto from(
      List<LessonGroupWithLesson> lessons, List<UserLessonDto> userLessonDtos) {
    LessonGroupWithLesson first = lessons.getFirst();

    return new UserLessonGroupDto(
        first.getLessonGroupId(),
        first.getCourseId(),
        first.getLessonGroupOrder(),
        first.getLessonGroupTitle(),
        first.getLessonGroupCreatedAt(),
        first.getLessonGroupUpdatedAt(),
        userLessonDtos);
  }
}
