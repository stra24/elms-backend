package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.course.Course;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** 更新用コースのコマンド。 */
@Getter
@AllArgsConstructor
public class CourseUpdateCommand {

  @NotNull private Integer id;
  @NotNull private BigDecimal courseOrder;
  @NotNull private String title;
  @Nullable private String description;
  @Nullable private String thumbnailUrl;
  @NotNull private LocalDateTime updatedAt;

  /**
   * Courseエンティティに変換する。
   *
   * @param course 更新対象のコース
   * @return 更新後のコースエンティティ
   */
  public Course toCourse(Course course) {
    return course.update(thumbnailUrl, title, description, courseOrder);
  }
}
