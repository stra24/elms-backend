package com.everrefine.elms.presentation.request;

import com.everrefine.elms.application.command.PasswordUpdateCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** パスワード変更リクエストに関するクラス。 */
@Data
public class PasswordUpdateRequest {

  @NotBlank private String currentPassword;
  @NotBlank private String newPassword;

  /**
   * Commandオブジェクトに変換する。
   *
   * @return パスワード変更Command
   */
  public PasswordUpdateCommand toCommand() {
    return new PasswordUpdateCommand(currentPassword, newPassword);
  }
}
