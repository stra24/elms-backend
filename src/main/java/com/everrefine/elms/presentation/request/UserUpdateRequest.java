package com.everrefine.elms.presentation.request;

import com.everrefine.elms.application.command.UserUpdateCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;

/** ユーザー更新リクエストに関するクラス。 */
@Data
public class UserUpdateRequest {

  @Schema(description = "ユーザーID", example = "1")
  private String userId;

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

  @Schema(description = "サムネイルURL", example = "https://example.com/thumbnail.png")
  private String thumbnailUrl;

  /**
   * Commandオブジェクトに変換する。
   *
   * @param userId ユーザーID
   * @return ユーザー更新Command
   */
  public UserUpdateCommand toCommand(Integer userId) {
    return new UserUpdateCommand(
        userId, realName, userName, emailAddress, thumbnailUrl, LocalDateTime.now());
  }
}
