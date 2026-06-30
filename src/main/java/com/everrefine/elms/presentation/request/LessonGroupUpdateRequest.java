package com.everrefine.elms.presentation.request;

import com.everrefine.elms.application.command.LessonGroupUpdateCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** レッスングループ更新リクエストに関するクラス。 */
@Data
public class LessonGroupUpdateRequest {

  @Schema(description = "レッスングループのタイトル（必須・100文字以内）", example = "第1章: 基礎編")
  @NotBlank(message = "レッスングループタイトルは必須です")
  @Size(max = 100, message = "レッスングループタイトルは100文字以内で入力してください")
  private String title;

  /**
   * Commandオブジェクトに変換する。
   *
   * @param lessonGroupId レッスングループID
   * @return レッスングループ更新Command
   */
  public LessonGroupUpdateCommand toCommand(Integer lessonGroupId) {
    return new LessonGroupUpdateCommand(lessonGroupId, title);
  }
}
