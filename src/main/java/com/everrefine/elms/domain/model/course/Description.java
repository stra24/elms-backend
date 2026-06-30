package com.everrefine.elms.domain.model.course;

import com.everrefine.elms.domain.exception.InvalidValueException;
import lombok.Value;
import org.springframework.lang.NonNull;

/** コース説明の値オブジェクト。 */
@Value
public class Description {

  // 最大文字数
  private static final int MAX_LENGTH = 1_000_000;
  String value;

  /**
   * コース説明を作成する。
   *
   * @param value 説明文字列（1,000,000文字以内）
   */
  public Description(@NonNull String value) {
    if (value == null || value.length() > MAX_LENGTH) {
      throw new InvalidValueException("説明は" + MAX_LENGTH + "文字以内で入力してください");
    }
    this.value = value;
  }
}
