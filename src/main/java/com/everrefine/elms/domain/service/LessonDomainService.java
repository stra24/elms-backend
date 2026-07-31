package com.everrefine.elms.domain.service;

import java.math.BigDecimal;
import java.util.UUID;

/** レッスンに関するドメインサービスインターフェース。 */
public interface LessonDomainService {

  /**
   * レッスンの並び順を発番する。 指定されたレッスングループ内で最大のlesson_order + 1024を返す。 レッスンが存在しない場合は1024を返す。
   *
   * @param lessonGroupId レッスングループID
   * @return 発番されたレッスンの並び順
   */
  BigDecimal issueLessonOrder(UUID lessonGroupId);

  /**
   * 新しいレッスンの並び順を計算する。 前後のレッスンの並び順から中間値を計算する。
   *
   * @param precedingOrder 前のレッスンの並び順（先頭に移動する場合はnull）
   * @param followingOrder 後のレッスンの並び順（末尾に移動する場合はnull）
   * @return 計算された新しい並び順
   */
  BigDecimal calculateNewOrder(BigDecimal precedingOrder, BigDecimal followingOrder);
}
