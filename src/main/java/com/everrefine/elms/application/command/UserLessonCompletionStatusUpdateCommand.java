package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.UserLesson;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** ユーザーレッスン完了状態更新用のコマンド。 */
@AllArgsConstructor
@Getter
public class UserLessonCompletionStatusUpdateCommand {

  private Integer userId;
  private Integer lessonId;
  private boolean isLessonCompleted;

  /**
   * 新規UserLessonエンティティに変換する。
   *
   * @return 新規ユーザーレッスンエンティティ
   */
  public UserLesson toNewUserLesson() {
    return UserLesson.create(this.userId, this.lessonId);
  }
}
