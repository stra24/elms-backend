package com.everrefine.elms.domain.model.user;

import com.everrefine.elms.domain.exception.InvalidValueException;
import java.util.regex.Pattern;
import lombok.Value;

/** Eメールアドレスの値オブジェクト。 */
@Value
public class EmailAddress {

  // Eメールアドレス形式であることを確認する正規表現
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$");
  String value;

  /**
   * メールアドレスを作成する。
   *
   * @param value メールアドレス文字列
   */
  public EmailAddress(String value) {
    if (value == null || !EMAIL_PATTERN.matcher(value).matches()) {
      throw new InvalidValueException("不正なメールアドレスです: " + value);
    }
    this.value = value;
  }
}
