package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.news.News;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** お知らせ更新用のコマンド。 */
@Getter
@AllArgsConstructor
public class NewsUpdateCommand {

  private Integer id;
  private String title;
  private String content;
  private LocalDateTime updatedAt;

  /**
   * Newsエンティティに変換する。
   *
   * @param news 更新対象のお知らせ
   * @return 更新後のお知らせエンティティ
   */
  public News toNews(News news) {
    return news.update(title, content);
  }
}
