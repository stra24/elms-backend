package com.everrefine.elms.domain.model.lesson;

import com.everrefine.elms.domain.model.Url;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.lang.Nullable;

/** 全レッスンを格納するエンティティ。 コース・レッスングループ・レッスンテーブルをJOINし、 クエリの結果をマッピングするために使用。 */
@Getter
@AllArgsConstructor
public class LessonWithCourseAndLessonGroup {

  @Id
  @Column("course_id")
  private final Integer courseId;

  @Column("course_title")
  private final Title courseTitle;

  @Column("lesson_group_id")
  private final Integer lessonGroupId;

  @Column("lesson_group_title")
  private final Title lessonGroupTitle;

  @Column("lesson_id")
  private final Integer lessonId;

  @Column("lesson_title")
  private final Title lessonTitle;

  @Nullable @Column("lesson_video_url")
  private final Url lessonVideoUrl;
}
