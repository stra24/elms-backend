package com.everrefine.elms.presentation.request;

import com.everrefine.elms.application.command.NewsCreateCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** お知らせ作成リクエストに関するクラス。 */
@Data
public class NewsCreateRequest {

  @Schema(description = "お知らせタイトル", example = "システムメンテナンスのお知らせ")
  @Size(max = 255)
  @NotBlank
  private String title;

  @Schema(description = "お知らせ本文", example = "4月1日にシステムメンテナンスを行います。")
  @NotBlank
  private String content;

  /**
   * Commandオブジェクトに変換する。
   *
   * @return お知らせ作成Command
   */
  public NewsCreateCommand toCommand() {
    return new NewsCreateCommand(null, title, content);
  }
}
