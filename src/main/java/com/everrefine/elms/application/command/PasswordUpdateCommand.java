package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** パスワード更新用のコマンド。 */
@Getter
@AllArgsConstructor
public class PasswordUpdateCommand {

  private String currentPassword;
  private String newPassword;

  /**
   * Userエンティティに変換する。
   *
   * @param user 更新対象のユーザー
   * @param newPassword 新しいパスワード（暗号化済み）
   * @return 更新後のユーザーエンティティ
   */
  public User toUser(User user, String newPassword) {
    return user.update(null, newPassword, null, null, null, null);
  }
}
