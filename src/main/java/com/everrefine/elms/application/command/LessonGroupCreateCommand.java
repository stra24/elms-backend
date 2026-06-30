package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.lesson.LessonGroup;
import java.math.BigDecimal;
import lombok.Data;

/** レッスングループ作成用のコマンド。 */
@Data
public class LessonGroupCreateCommand {

  private final Integer courseId;
  private final String title;

  /**
   * LessonGroupエンティティに変換する。
   *
   * @param lessonGroupOrder レッスングループ順序
   * @return レッスングループエンティティ
   */
  public LessonGroup toLessonGroup(BigDecimal lessonGroupOrder) {
    return LessonGroup.create(courseId, lessonGroupOrder, title);
  }
}
