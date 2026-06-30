package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.lesson.Lesson;
import java.math.BigDecimal;
import lombok.Data;

/** レッスン作成用のコマンド。 */
@Data
public class LessonCreateCommand {

  private final Integer courseId;
  private final Integer lessonGroupId;
  private final String title;
  private final String content;
  private final String videoUrl;
  private final BigDecimal lessonOrder;

  /**
   * Lessonエンティティに変換する。
   *
   * @param lessonOrder レッスン順序
   * @return レッスンエンティティ
   */
  public Lesson toLesson(BigDecimal lessonOrder) {
    return Lesson.create(lessonGroupId, courseId, lessonOrder, title, content, videoUrl);
  }
}
