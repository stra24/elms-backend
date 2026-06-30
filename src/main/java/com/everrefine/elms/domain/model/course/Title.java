package com.everrefine.elms.domain.model.course;

import com.everrefine.elms.domain.exception.InvalidValueException;
import lombok.Value;

/** コース名の値オブジェクト。 */
@Value
public class Title {

  // 最大文字数
  private static final int MAX_LENGTH = 255;
  String value;

  /**
   * コース名を作成する。
   *
   * @param value コース名文字列（255文字以内）
   */
  public Title(String value) {
    if (value == null || value.length() > MAX_LENGTH) {
      throw new InvalidValueException("タイトルは" + MAX_LENGTH + "文字以内で入力してください");
    }
    this.value = value;
  }
}
