package com.everrefine.elms.domain.model;

import java.math.BigDecimal;
import lombok.Value;

/** 順番を示す値オブジェクト。 */
@Value
public class Order {
  public static BigDecimal FIRST_ORDER = new BigDecimal("1024");
  public static BigDecimal INTERVAL_ORDER = new BigDecimal("1024");
  BigDecimal value;

  /**
   * 最初の並び順を返す。
   *
   * @return 最初の並び順
   */
  public static Order getFirst() {
    return new Order(FIRST_ORDER);
  }

  /**
   * 次の並び順を返す。
   *
   * @return 現在の値にINTERVAL_ORDERを加えた並び順
   */
  public Order getNext() {
    return new Order(INTERVAL_ORDER.add(getValue()));
  }
}
