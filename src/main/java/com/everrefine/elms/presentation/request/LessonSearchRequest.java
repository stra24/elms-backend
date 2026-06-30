package com.everrefine.elms.presentation.request;

import com.everrefine.elms.application.command.LessonSearchCommand;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import lombok.Data;

/** レッスン検索リクエストに関するクラス。 */
@Data
public class LessonSearchRequest {

  private int pageNum = 1;
  private int pageSize = 10;

  @Nullable private String courseId;

  @Nullable private String lessonGroupId;

  @Nullable private String title;

  @Nullable LocalDate createdDateFrom;

  @Nullable LocalDate createdDateTo;

  /**
   * Commandオブジェクトに変換する。
   *
   * @return レッスン検索Command
   */
  public LessonSearchCommand toCommand() {
    return new LessonSearchCommand(
        pageNum, pageSize, courseId, lessonGroupId, title, createdDateFrom, createdDateTo);
  }
}
