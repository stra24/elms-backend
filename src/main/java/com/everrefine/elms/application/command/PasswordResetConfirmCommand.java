package com.everrefine.elms.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** パスワードリセット確定用のコマンド。 */
@Getter
@AllArgsConstructor
public class PasswordResetConfirmCommand {
  private String token;
  private String newPassword;
}
