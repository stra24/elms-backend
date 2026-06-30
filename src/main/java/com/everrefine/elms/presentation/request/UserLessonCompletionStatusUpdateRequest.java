package com.everrefine.elms.presentation.request;

import com.everrefine.elms.application.command.UserLessonCompletionStatusUpdateCommand;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/** ユーザーレッスン完了状態更新リクエストに関するクラス。 */
@Data
public class UserLessonCompletionStatusUpdateRequest {

  @Schema(description = "レッスン完了フラグ（true: 完了, false: 未完了）", example = "true")
  @JsonProperty("isLessonCompleted")
  private boolean isLessonCompleted;

  /**
   * Commandオブジェクトに変換する。
   *
   * @param userId ユーザーID
   * @param lessonId レッスンID
   * @return ユーザーレッスン完了状態更新Command
   */
  public UserLessonCompletionStatusUpdateCommand toCommand(Integer userId, Integer lessonId) {
    return new UserLessonCompletionStatusUpdateCommand(userId, lessonId, this.isLessonCompleted);
  }
}
