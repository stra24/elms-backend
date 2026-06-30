package com.everrefine.elms.presentation.request;

import com.everrefine.elms.application.command.UserSearchCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

/** ユーザー検索リクエストに関するクラス。 */
@Data
public class UserSearchRequest {

  @Schema(description = "ページ番号（1始まり）", example = "1")
  @Positive private int pageNum = 1;

  @Schema(description = "1ページ当たりの件数", example = "10")
  @Positive private int pageSize = 10;

  @Schema(description = "ユーザーID（部分一致）", example = "1")
  @Pattern(regexp = "[0-9]*")
  private String userId;

  @Schema(description = "ユーザーロール（GENERAL / ADMIN）", example = "GENERAL")
  @Size(max = 10)
  private String userRole;

  @Schema(description = "本名（部分一致）", example = "山田")
  @Size(max = 50)
  private String realName;

  @Schema(description = "ユーザー名（部分一致）", example = "yamada")
  @Size(max = 50)
  private String userName;

  @Schema(description = "メールアドレス（部分一致）", example = "yamada@example.com")
  @Size(max = 255)
  private String emailAddress;

  @Schema(description = "登録日From", example = "2024-01-01")
  LocalDate createdDateFrom;

  @Schema(description = "登録日To", example = "2024-12-31")
  LocalDate createdDateTo;

  /**
   * Commandオブジェクトに変換する。
   *
   * @return ユーザー検索Command
   */
  public UserSearchCommand toCommand() {
    return new UserSearchCommand(
        pageNum,
        pageSize,
        userId,
        userRole,
        realName,
        userName,
        emailAddress,
        createdDateFrom,
        createdDateTo);
  }
}
