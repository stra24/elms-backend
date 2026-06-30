package com.everrefine.elms.domain.model.passwordreset;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

@Getter
@AllArgsConstructor
@Table("password_reset_tokens")
/** パスワードリセットトークンのエンティティ。 */
public class PasswordResetToken {

  private static final int EXPIRY_MINUTES = 30;

  @Id private final Integer id;

  private final Integer userId;

  private final String token;

  private final LocalDateTime expiresAt;

  @Nullable private final LocalDateTime usedAt;

  /**
   * 新規のパスワードリセットトークンを作成する。有効期限は30分。
   *
   * @param userId ユーザーID
   * @return 新規作成されたパスワードリセットトークン
   */
  public static PasswordResetToken create(Integer userId) {
    return new PasswordResetToken(
        null,
        userId,
        UUID.randomUUID().toString(),
        LocalDateTime.now().plusMinutes(EXPIRY_MINUTES),
        null);
  }

  /**
   * トークンが有効期限切れかどうかを判定する。
   *
   * @return 有効期限切れの場合はtrue
   */
  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiresAt);
  }

  /**
   * トークンが使用済みかどうかを判定する。
   *
   * @return 使用済みの場合はtrue
   */
  public boolean isUsed() {
    return usedAt != null;
  }

  /**
   * トークンを使用済みとしてマークする。
   *
   * @return 使用済みに更新されたパスワードリセットトークン
   */
  public PasswordResetToken markAsUsed() {
    return new PasswordResetToken(id, userId, token, expiresAt, LocalDateTime.now());
  }
}
