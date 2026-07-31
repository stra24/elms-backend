package com.everrefine.elms.domain.model.lesson;

import com.everrefine.elms.domain.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/** レッスングループのドメインモデル。 */
public record LessonGroup(
    UUID id,
    UUID courseId,
    Order lessonGroupOrder,
    LessonTitle title,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  /**
   * 新規作成用のレッスングループを作成する。
   *
   * @param courseId コースID
   * @param lessonGroupOrder レッスングループの並び順
   * @param title レッスングループタイトル
   * @return 新規作成用のレッスングループ
   */
  public static LessonGroup create(UUID courseId, BigDecimal lessonGroupOrder, String title) {
    return new LessonGroup(
        null,
        courseId,
        new Order(lessonGroupOrder),
        new LessonTitle(title),
        LocalDateTime.now(),
        LocalDateTime.now());
  }

  /**
   * レッスングループを更新する。
   *
   * @param title 新しいタイトル
   * @return 更新後のレッスングループ
   */
  public LessonGroup update(String title) {
    return new LessonGroup(
        this.id,
        this.courseId,
        this.lessonGroupOrder,
        new LessonTitle(title),
        this.createdAt,
        LocalDateTime.now());
  }
}
