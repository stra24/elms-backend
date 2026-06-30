package com.everrefine.elms.domain.repository;

import com.everrefine.elms.domain.model.UserLesson;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** ユーザーレッスンのリポジトリインターフェース。 */
public interface UserLessonRepository {

  /**
   * ユーザーIDとレッスンIDでユーザーレッスンを取得する。
   *
   * @param userId ユーザーID
   * @param lessonId レッスンID
   * @return ユーザーレッスン（存在しない場合は空）
   */
  Optional<UserLesson> findByUserIdAndLessonId(Integer userId, Integer lessonId);

  /**
   * ユーザーレッスンを保存する。
   *
   * @param userLesson 保存するユーザーレッスン
   */
  void save(UserLesson userLesson);

  /**
   * ユーザーIDとレッスンIDでユーザーレッスンを削除する。
   *
   * @param userId ユーザーID
   * @param lessonId レッスンID
   */
  void deleteByUserIdAndLessonId(Integer userId, Integer lessonId);

  /**
   * ユーザーの完了レッスン総数を取得する。
   *
   * @param userId ユーザーID
   * @return 完了レッスン総数
   */
  int countAllByUserId(Integer userId);

  /**
   * 複数ユーザーの完了レッスン数をMap形式で取得する。
   *
   * @param userIds ユーザーIDのリスト
   * @return ユーザーIDをキー、完了レッスン数を値とするMap
   */
  Map<Integer, Integer> countByUserIds(List<Integer> userIds);

  /**
   * ユーザーのコース内完了レッスン数を取得する。
   *
   * @param userId ユーザーID
   * @param courseId コースID
   * @return 完了レッスン数
   */
  int countCompletedLessonsByUserIdAndCourseId(Integer userId, Integer courseId);

  /**
   * ユーザーが完了したレッスンIDのセットを取得する。
   *
   * @param userId ユーザーID
   * @param lessonIds 検索対象のレッスンIDのセット
   * @return 完了済みレッスンIDのセット
   */
  Set<Integer> findLessonIdByUserIdAndLessonIdIn(Integer userId, Set<Integer> lessonIds);
}
