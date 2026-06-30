package com.everrefine.elms.domain.model;

import com.everrefine.elms.domain.exception.InvalidValueException;
import lombok.Value;

/** URLの値オブジェクト。 画像や動画のURLとして使うことを想定している。 */
@Value
public class Url {

  public static final int MAX_LENGTH = 2048;

  String value;

  /**
   * URLの値オブジェクトを生成する。
   *
   * @param value URL文字列
   */
  public Url(String value) {
    if (value == null) {
      throw new InvalidValueException("URLはnullにできません");
    }

    if (value.length() > MAX_LENGTH) {
      throw new InvalidValueException("URLは" + MAX_LENGTH + "文字以内で入力してください");
    }

    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
