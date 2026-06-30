package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.course.Course;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

/** 新規作成用コースのコマンド。 */
@Getter
@AllArgsConstructor
public class CourseCreateCommand {

  @Nullable private Integer id;
  @Nullable private String thumbnailUrl;
  @NotNull private String title;
  @Nullable private String description;

  /**
   * Courseエンティティに変換する。
   *
   * @param courseOrder コース順序
   * @return コースエンティティ
   */
  public Course toCourse(java.math.BigDecimal courseOrder) {
    return Course.create(thumbnailUrl, title, description, courseOrder);
  }
}
