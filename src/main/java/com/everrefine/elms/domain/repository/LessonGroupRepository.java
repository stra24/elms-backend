package com.everrefine.elms.domain.repository;

import com.everrefine.elms.domain.model.lesson.LessonGroup;
import java.math.BigDecimal;
import java.util.Optional;

/** レッスングループのリポジトリインターフェース。 */
public interface LessonGroupRepository {

  /**
   * レッスングループを作成する。
   *
   * @param lessonGroup 作成するレッスングループ
   * @return 作成されたレッスングループ
   */
  LessonGroup createLessonGroup(LessonGroup lessonGroup);

  /**
   * レッスングループを更新する。
   *
   * @param lessonGroup 更新するレッスングループ
   * @return 更新されたレッスングループ
   */
  LessonGroup updateLessonGroup(LessonGroup lessonGroup);

  /**
   * コース内の最大lesson_group_orderを取得する。
   *
   * @param courseId コースID
   * @return 最大lesson_group_order（レッスングループが存在しない場合は空）
   */
  Optional<BigDecimal> findMaxLessonGroupOrderByCourseId(Integer courseId);

  /**
   * IDでレッスングループを取得する。
   *
   * @param id レッスングループID
   * @return レッスングループ（存在しない場合は空）
   */
  Optional<LessonGroup> findLessonGroupById(Integer id);

  /**
   * IDでレッスングループを削除する。
   *
   * @param id レッスングループID
   */
  void deleteLessonGroupById(Integer id);
}
