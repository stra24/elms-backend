package com.everrefine.elms.application.command;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** ログイン履歴作成用のコマンド。 */
@Getter
@AllArgsConstructor
public class LoginHistoryCreateCommand {

  @NotNull String email;
}
