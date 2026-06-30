package com.everrefine.elms.domain.model.lesson;

import jakarta.annotation.Nullable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** レッスン検索条件の値オブジェクト。 */
@Getter
@AllArgsConstructor
public class LessonSearchCriteria {

  private int pageNum;
  private int pageSize;
  @Nullable private String courseId;
  @Nullable private String lessonGroupId;
  @Nullable private String title;
  @Nullable private LocalDate createdDateFrom;
  @Nullable private LocalDate createdDateTo;
}
