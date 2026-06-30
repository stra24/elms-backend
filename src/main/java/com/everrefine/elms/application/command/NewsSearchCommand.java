package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.news.NewsSearchCondition;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** お知らせ検索用のコマンド。 */
@Getter
@AllArgsConstructor
public class NewsSearchCommand {

  private int pageNum;
  private int pageSize;
  private String title;
  private LocalDate createdDateFrom;
  private LocalDate createdDateTo;

  /**
   * NewsSearchConditionに変換する。
   *
   * @return お知らせ検索条件
   */
  public NewsSearchCondition toSearchCondition() {
    return new NewsSearchCondition(pageNum, pageSize, title, createdDateFrom, createdDateTo);
  }
}
