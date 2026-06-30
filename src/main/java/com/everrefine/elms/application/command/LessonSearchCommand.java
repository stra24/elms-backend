package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.lesson.LessonSearchCriteria;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** レッスン検索用のコマンド。 */
@Getter
@AllArgsConstructor
public class LessonSearchCommand {

  private int pageNum;
  private int pageSize;
  @Nullable private String courseId;
  @Nullable private String lessonGroupId;
  @Nullable private String title;
  @Nullable private LocalDate createdDateFrom;
  @Nullable private LocalDate createdDateTo;

  /**
   * LessonSearchCriteriaに変換する。
   *
   * @return レッスン検索条件
   */
  public LessonSearchCriteria toCriteria() {
    return new LessonSearchCriteria(
        pageNum, pageSize, courseId, lessonGroupId, title, createdDateFrom, createdDateTo);
  }
}
