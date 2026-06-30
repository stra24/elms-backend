package com.everrefine.elms.application.command;

import lombok.Data;

/** レッスン順序更新用のコマンド。 */
@Data
public class LessonOrderUpdateCommand {

  private final Integer lessonId;
  private final Integer precedingLessonId;
  private final Integer followingLessonId;
}
