package com.everrefine.elms.domain.model.lesson;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

/** レッスングループとレッスンの情報を格納するクラス。 JOINクエリの結果をマッピングするために使用。 */
@Getter
@AllArgsConstructor
@Table("lesson_groups")
public class LessonGroupWithLesson {

  @Id
  @Column("lesson_id")
  private final Integer lessonId;

  @Column("lesson_title")
  private final String lessonTitle;

  @Column("lesson_order")
  private final BigDecimal lessonOrder;

  @Nullable @Column("lesson_content")
  private final String lessonContent;

  @Nullable @Column("lesson_video_url")
  private final String lessonVideoUrl;

  @Column("lesson_created_at")
  private final LocalDateTime lessonCreatedAt;

  @Column("lesson_updated_at")
  private final LocalDateTime lessonUpdatedAt;

  @Column("lesson_group_id")
  private final Integer lessonGroupId;

  @Column("course_id")
  private final Integer courseId;

  @Column("lesson_group_title")
  private final String lessonGroupTitle;

  @Column("lesson_group_order")
  private final BigDecimal lessonGroupOrder;

  @Column("lesson_group_created_at")
  private final LocalDateTime lessonGroupCreatedAt;

  @Column("lesson_group_updated_at")
  private final LocalDateTime lessonGroupUpdatedAt;
}
