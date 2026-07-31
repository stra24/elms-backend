package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.lesson.Lesson;
import com.everrefine.elms.domain.model.lesson.LessonGroup;
import java.math.BigDecimal;
import java.util.UUID;

/** CSV取込用レッスン行のコマンド。CSVの1行分のレッスン情報を保持する。 */
public record LessonImportRowCommand(
    String lessonGroupTitle, String lessonTitle, String content, String videoUrl) {

  /**
   * 保存用レッスングループを作成する。
   *
   * @param courseId コースID
   * @param lessonGroupOrder レッスングループ表示順
   * @return 保存用レッスングループ
   */
  public LessonGroup toLessonGroup(UUID courseId, BigDecimal lessonGroupOrder) {
    return LessonGroup.create(courseId, lessonGroupOrder, lessonGroupTitle);
  }

  /**
   * 保存用レッスンを作成する。
   *
   * @param lessonGroupId レッスングループID
   * @param courseId コースID
   * @param lessonOrder レッスン表示順
   * @return 保存用レッスン
   */
  public Lesson toLesson(UUID lessonGroupId, UUID courseId, BigDecimal lessonOrder) {
    return Lesson.create(lessonGroupId, courseId, lessonOrder, lessonTitle, content, videoUrl);
  }
}
