package com.everrefine.elms.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/** ユーザーレッスンのエンティティ。 */
@Getter
@AllArgsConstructor
@Table("user_lessons")
public class UserLesson {

  @Id private final Integer id;

  @Column("user_id")
  private Integer userId;

  @Column("lesson_id")
  private Integer lessonId;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  /**
   * 新規作成用のユーザーレッスンを作成する。
   *
   * @param userId ユーザーID
   * @param lessonId レッスンID
   * @return 新規作成用のユーザーレッスン
   */
  public static UserLesson create(Integer userId, Integer lessonId) {
    LocalDateTime now = LocalDateTime.now();
    return new UserLesson(null, userId, lessonId, now, now);
  }

  /**
   * updated_atを現在時刻に更新する。
   *
   * @return 更新されたユーザーレッスン
   */
  public UserLesson update() {
    return new UserLesson(this.id, this.userId, this.lessonId, this.createdAt, LocalDateTime.now());
  }
}
