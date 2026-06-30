package com.everrefine.elms.domain.model.course;

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

/** コースのエンティティ。 */
@Getter
@AllArgsConstructor
@Table("courses")
public class Course {

  @Id private final Integer id;

  @Nullable @Column("thumbnail_url")
  private Url thumbnailUrl;

  private Title title;
  @Nullable private Description description;

  @Column("course_order")
  private Order courseOrder;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  /**
   * 新規作成用のコースを作成する。
   *
   * @param thumbnailUrl サムネイル画像URL
   * @param title タイトル
   * @param description 説明
   * @param courseOrder コースの並び順
   * @return 新規作成用のコース
   */
  public static Course create(
      @Nullable String thumbnailUrl,
      String title,
      @Nullable String description,
      BigDecimal courseOrder) {
    return new Course(
        null,
        thumbnailUrl == null ? null : new Url(thumbnailUrl),
        new Title(title),
        description == null ? null : new Description(description),
        new Order(courseOrder),
        LocalDateTime.now(),
        LocalDateTime.now());
  }

  /**
   * 更新用のコースを作成する。
   *
   * @param thumbnailUrl サムネイル画像URL
   * @param title タイトル
   * @param description 説明
   * @param courseOrder コースの並び順
   * @return 更新用のコース
   */
  public Course update(
      @Nullable String thumbnailUrl,
      String title,
      @Nullable String description,
      BigDecimal courseOrder) {
    return new Course(
        id,
        thumbnailUrl == null ? null : new Url(thumbnailUrl),
        new Title(title),
        description == null ? null : new Description(description),
        new Order(courseOrder),
        createdAt,
        LocalDateTime.now());
  }
}
