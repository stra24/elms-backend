package com.everrefine.elms.domain.model.user;

import com.everrefine.elms.domain.exception.InvalidValueException;
import lombok.Value;

/** 氏名の値オブジェクト。 */
@Value
public class RealName {

  // 最大文字数
  private static final int MAX_LENGTH = 50;
  String value;

  /**
   * 氏名を作成する。
   *
   * @param value 氏名文字列（50文字以内）
   */
  public RealName(String value) {
    if (value == null || value.length() > MAX_LENGTH) {
      throw new InvalidValueException("氏名は" + MAX_LENGTH + "文字以内で入力してください");
    }
    this.value = value;
  }
}
