package com.everrefine.elms.presentation.request;

import com.everrefine.elms.application.command.NewsSearchCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

/** お知らせ検索リクエストに関するクラス。 */
@Data
public class NewsSearchRequest {

  @Schema(description = "ページ番号（1始まり）", example = "1")
  @Positive private int pageNum = 1;

  @Schema(description = "1ページ当たりの件数", example = "10")
  @Positive private int pageSize = 10;

  @Schema(description = "タイトル（部分一致）", example = "メンテナンス")
  @Size(max = 255)
  private String title;

  @Schema(description = "登録日From", example = "2024-01-01")
  private LocalDate createdDateFrom;

  @Schema(description = "登録日To", example = "2024-12-31")
  private LocalDate createdDateTo;

  /**
   * Commandオブジェクトに変換する。
   *
   * @return お知らせ検索Command
   */
  public NewsSearchCommand toCommand() {
    return new NewsSearchCommand(pageNum, pageSize, title, createdDateFrom, createdDateTo);
  }
}
