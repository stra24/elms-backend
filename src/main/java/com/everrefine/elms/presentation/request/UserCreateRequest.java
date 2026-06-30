package com.everrefine.elms.presentation.request;

import com.everrefine.elms.application.command.UserCreateCommand;
import com.everrefine.elms.domain.model.user.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** ユーザー作成リクエストに関するクラス。 */
@Data
public class UserCreateRequest {

  @Schema(description = "本名", example = "山田太郎")
  @NotBlank
  @Size(max = 50)
  private String realName;

  @Schema(description = "ユーザー名", example = "yamada_taro")
  @NotBlank
  @Size(max = 50)
  private String userName;

  @Schema(description = "メールアドレス", example = "yamada@example.com")
  @NotBlank
  @Size(max = 255)
  private String emailAddress;

  @Schema(description = "パスワード", example = "password123")
  @NotBlank
  @Size(max = 255)
  private String password;

  @Schema(description = "確認用パスワード（passwordと一致する必要があります）", example = "password123")
  @NotBlank
  @Size(max = 255)
  private String confirmPassword;

  @Schema(description = "サムネイルURL", example = "https://example.com/thumbnail.png")
  private String thumbnailUrl;

  @Schema(description = "ユーザーロール（GENERAL: 一般, ADMIN: 管理者）", example = "GENERAL")
  @NotNull private UserRole userRole;

  /**
   * Commandオブジェクトに変換する。
   *
   * @return ユーザー作成Command
   */
  public UserCreateCommand toCommand() {
    return new UserCreateCommand(
        null, realName, userName, emailAddress, password, confirmPassword, thumbnailUrl, userRole);
  }
}
