package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.lesson.LessonGroup;
import lombok.Data;

/** レッスングループ更新用のコマンド。 */
@Data
public class LessonGroupUpdateCommand {

  private final Integer id;
  private final String title;

  /**
   * LessonGroupエンティティに変換する。
   *
   * @param lessonGroup 更新対象のレッスングループ
   * @return 更新後のレッスングループエンティティ
   */
  public LessonGroup toLessonGroup(LessonGroup lessonGroup) {
    return lessonGroup.update(title);
  }
}
