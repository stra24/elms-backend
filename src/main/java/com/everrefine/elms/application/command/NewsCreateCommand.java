package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.news.News;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** お知らせ作成用のコマンド。 */
@Getter
@AllArgsConstructor
public class NewsCreateCommand {

  private Integer id;
  private String title;
  private String content;

  /**
   * Newsエンティティに変換する。
   *
   * @return お知らせエンティティ
   */
  public News toNews() {
    return News.create(title, content);
  }
}
