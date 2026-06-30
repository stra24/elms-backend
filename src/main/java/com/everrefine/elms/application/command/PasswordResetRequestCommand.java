package com.everrefine.elms.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** パスワードリセットリクエスト用のコマンド。 */
@Getter
@AllArgsConstructor
public class PasswordResetRequestCommand {
  private String emailAddress;
}
