package com.everrefine.elms.domain.model.lesson;

import com.everrefine.elms.domain.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/** レッスングループのエンティティ。 */
@Getter
@AllArgsConstructor
@Table("lesson_groups")
public class LessonGroup {

  @Id private final Integer id;

  @Column("course_id")
  private Integer courseId;

  @Column("lesson_group_order")
  private Order lessonGroupOrder;

  private Title title;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  /**
   * 新規作成用のレッスングループを作成する。
   *
   * @param courseId コースID
   * @param lessonGroupOrder レッスングループの並び順
   * @param title レッスングループタイトル
   * @return 新規作成用のレッスングループ
   */
  public static LessonGroup create(Integer courseId, BigDecimal lessonGroupOrder, String title) {
    return new LessonGroup(
        null,
        courseId,
        new Order(lessonGroupOrder),
        new Title(title),
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
        new Title(title),
        this.createdAt,
        LocalDateTime.now());
  }
}
