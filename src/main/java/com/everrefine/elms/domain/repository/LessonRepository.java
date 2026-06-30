package com.everrefine.elms.domain.repository;

import com.everrefine.elms.domain.model.lesson.Lesson;
import com.everrefine.elms.domain.model.lesson.LessonGroupWithLesson;
import com.everrefine.elms.domain.model.lesson.LessonSearchCriteria;
import com.everrefine.elms.domain.model.lesson.LessonWithCourseAndLessonGroup;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** レッスンのリポジトリインターフェース。 */
public interface LessonRepository {

  /**
   * IDでレッスンを取得する。
   *
   * @param lessonId レッスンID
   * @return レッスン（存在しない場合は空）
   */
  Optional<Lesson> findById(Integer lessonId);

  /**
   * 複数のIDでレッスン一覧を取得する。
   *
   * @param lessonIds レッスンIDのリスト
   * @return レッスン一覧
   */
  List<Lesson> findByIdIn(List<Integer> lessonIds);

  /**
   * 検索条件に合致するレッスン一覧を取得する。
   *
   * @param criteria レッスン検索条件
   * @return レッスン一覧
   */
  List<Lesson> findLessons(LessonSearchCriteria criteria);

  /**
   * 検索条件に合致するレッスンの件数を取得する。
   *
   * @param criteria レッスン検索条件
   * @return レッスンの件数
   */
  int countLessons(LessonSearchCriteria criteria);

  /**
   * レッスングループIDに紐づくレッスン一覧を取得する。
   *
   * @param lessonGroupId レッスングループID
   * @return レッスン一覧
   */
  List<Lesson> findLessonsByLessonGroupId(Integer lessonGroupId);

  /**
   * コースIDに紐づくレッスングループごとのレッスン一覧を取得する。
   *
   * @param courseId コースID
   * @return レッスングループとレッスンの一覧
   */
  List<LessonGroupWithLesson> findLessonsGroupedByLessonGroup(Integer courseId);

  /**
   * レッスンを作成する。
   *
   * @param lesson 作成するレッスン
   * @return 作成されたレッスン
   */
  Lesson createLesson(Lesson lesson);

  /**
   * レッスンを更新する。
   *
   * @param lesson 更新するレッスン
   * @return 更新されたレッスン
   */
  Lesson updateLesson(Lesson lesson);

  /**
   * レッスングループ内の最大lesson_orderを取得する。
   *
   * @param lessonGroupId レッスングループID
   * @return 最大lesson_order（レッスンが存在しない場合は空）
   */
  Optional<BigDecimal> findMaxLessonOrderByLessonGroupId(Integer lessonGroupId);

  /**
   * IDでレッスンを削除する。
   *
   * @param lessonId レッスンID
   */
  void deleteLessonById(Integer lessonId);

  /**
   * レッスンの総件数を取得する。
   *
   * @return レッスンの総件数
   */
  int countAllLessons();

  /**
   * 指定プレフィックスで始まる動画URLを取得する。
   *
   * @param prefix URLのプレフィックス
   * @return 動画URLのリスト
   */
  List<String> findByVideoUrlStartingWith(String prefix);

  /**
   * コース・レッスングループ情報を含む全レッスン一覧を取得する。
   *
   * @return 全レッスン一覧
   */
  List<LessonWithCourseAndLessonGroup> findAllLessons();
}
