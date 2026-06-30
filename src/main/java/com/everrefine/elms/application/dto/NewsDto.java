package com.everrefine.elms.application.dto;

import com.everrefine.elms.domain.model.news.News;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** お知らせDTOに関するクラス。 */
@AllArgsConstructor
@Getter
public class NewsDto {

  @Schema(description = "お知らせID", example = "1")
  private final Integer id;

  @Schema(description = "お知らせタイトル", example = "システムメンテナンスのお知らせ")
  private final String title;

  @Schema(description = "お知らせ本文", example = "4月1日にシステムメンテナンスを行います。")
  private final String content;

  @Schema(description = "登録日", example = "2024-01-01")
  private final LocalDate createdAt;

  @Schema(description = "更新日", example = "2024-06-01")
  private final LocalDate updatedAt;

  /**
   * NewsエンティティからNewsDtoを生成する。
   *
   * @param news お知らせエンティティ
   * @return お知らせDTO
   */
  public static NewsDto from(News news) {
    return new NewsDto(
        news.getId(),
        news.getTitle().getValue(),
        news.getContent().getValue(),
        news.getCreatedAt().toLocalDate(),
        news.getUpdatedAt().toLocalDate());
  }
}
