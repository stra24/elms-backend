package com.everrefine.elms.presentation.request;

import com.everrefine.elms.application.command.LessonGroupCreateCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** レッスングループ作成リクエストに関するクラス。 */
@Data
public class LessonGroupCreateRequest {

  @Schema(description = "レッスングループのタイトル（必須・100文字以内）", example = "第1章: 基礎編")
  @NotBlank(message = "レッスングループタイトルは必須です")
  @Size(max = 100, message = "レッスングループタイトルは100文字以内で入力してください")
  private String title;

  /**
   * Commandオブジェクトに変換する。
   *
   * @param courseId コースID
   * @return レッスングループ作成Command
   */
  public LessonGroupCreateCommand toCommand(Integer courseId) {
    return new LessonGroupCreateCommand(courseId, title);
  }
}
