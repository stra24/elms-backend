package com.everrefine.elms.presentation.request;

import com.everrefine.elms.application.command.LessonOrderUpdateCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/** レッスン並び順更新リクエストに関するクラス。 */
@Data
public class LessonOrderUpdateRequest {

  @Schema(description = "移動先の直前となるレッスンID（先頭に移動する場合はnull）", example = "3")
  @Positive(message = "直前レッスンIDは1以上で指定してください") private Integer precedingLessonId;

  @Schema(description = "移動先の直後となるレッスンID（末尾に移動する場合はnull）", example = "5")
  @Positive(message = "直後レッスンIDは1以上で指定してください") private Integer followingLessonId;

  /**
   * Commandオブジェクトに変換する。
   *
   * @param lessonId レッスンID
   * @return レッスン並び順更新Command
   */
  public LessonOrderUpdateCommand toCommand(Integer lessonId) {
    return new LessonOrderUpdateCommand(lessonId, precedingLessonId, followingLessonId);
  }
}
