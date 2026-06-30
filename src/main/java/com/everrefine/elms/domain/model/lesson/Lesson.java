package com.everrefine.elms.domain.model.lesson;

import com.everrefine.elms.domain.model.Order;
import com.everrefine.elms.domain.model.Url;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

/** レッスンのエンティティ。 */
@Getter
@AllArgsConstructor
@Table("lessons")
public class Lesson {

  @Id private final Integer id;

  @Column("lesson_group_id")
  private Integer lessonGroupId;

  @Column("course_id")
  private Integer courseId;

  @Column("lesson_order")
  private Order lessonOrder;

  private Title title;
  @Nullable private Content content;

  @Nullable @Column("video_url")
  private Url videoUrl;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  /**
   * 新規作成用のレッスンを作成する。
   *
   * @param lessonGroupId レッスングループID
   * @param courseId コースID
   * @param lessonOrder レッスンの並び順
   * @param title レッスンタイトル
   * @param content レッスンの本文
   * @param videoUrl レッスンの動画URL
   * @return 新規作成用のレッスン
   */
  public static Lesson create(
      Integer lessonGroupId,
      Integer courseId,
      BigDecimal lessonOrder,
      String title,
      String content,
      String videoUrl) {
    return new Lesson(
        null,
        lessonGroupId,
        courseId,
        new Order(lessonOrder),
        new Title(title),
        content == null ? null : new Content(content),
        videoUrl == null ? null : new Url(videoUrl),
        LocalDateTime.now(),
        LocalDateTime.now());
  }

  /**
   * 更新用のレッスンを作成する。
   *
   * @param title レッスンタイトル
   * @param content レッスンの本文
   * @param videoUrl レッスンの動画URL
   * @return 更新用のレッスン
   */
  public Lesson update(String title, String content, String videoUrl) {
    return new Lesson(
        this.id,
        this.lessonGroupId,
        this.courseId,
        this.lessonOrder,
        title == null ? this.title : new Title(title),
        content == null ? this.content : new Content(content),
        videoUrl == null ? this.videoUrl : new Url(videoUrl),
        this.createdAt,
        LocalDateTime.now());
  }

  /**
   * レッスンの並び順を変更する。
   *
   * @param newOrder 新しい並び順
   * @return 並び順が変更されたレッスン
   */
  public Lesson updateOrder(BigDecimal newOrder) {
    return new Lesson(
        this.id,
        this.lessonGroupId,
        this.courseId,
        new Order(newOrder),
        this.title,
        this.content,
        this.videoUrl,
        this.createdAt,
        LocalDateTime.now());
  }
}
