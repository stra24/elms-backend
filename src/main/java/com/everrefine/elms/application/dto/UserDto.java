package com.everrefine.elms.application.dto;

import com.everrefine.elms.domain.model.user.User;
import com.everrefine.elms.domain.model.user.UserLoginHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** ユーザーDTOに関するクラス。 */
@AllArgsConstructor
@Getter
public class UserDto {

  @Schema(description = "ユーザーID", example = "1")
  private final Integer id;

  @Schema(description = "メールアドレス", example = "yamada@example.com")
  private final String emailAddress;

  @Schema(description = "本名", example = "山田太郎")
  private final String realName;

  @Schema(description = "ユーザー名", example = "yamada_taro")
  private final String userName;

  @Schema(description = "サムネイルURL", example = "https://example.com/thumbnail.png")
  private final String thumbnailUrl;

  @Schema(description = "ユーザーロール（一般 / 管理者）", example = "一般")
  private final String userRole;

  @Schema(description = "登録日時", example = "2024-01-01T09:00:00")
  private final LocalDateTime createdAt;

  @Schema(description = "最終ログイン日時", example = "2024-06-01T10:30:00")
  private final LocalDateTime lastLoginAt;

  @Schema(description = "受講進捗率（0〜100）", example = "75.00")
  private final BigDecimal progressRate;

  /**
   * UserエンティティからUserDtoを生成する。
   *
   * @param user ユーザーエンティティ
   * @param userLoginHistory ユーザーログイン履歴
   * @param progressRate 受講進捗率
   * @return ユーザーDTO
   */
  public static UserDto from(
      User user, UserLoginHistory userLoginHistory, BigDecimal progressRate) {

    return new UserDto(
        user.getId(),
        user.getEmailAddress().getValue(),
        user.getRealName().getValue(),
        user.getUserName().getValue(),
        user.getThumbnailUrl() == null ? null : user.getThumbnailUrl().getValue(),
        user.getUserRole().getRoleName(),
        user.getCreatedAt(),
        userLoginHistory == null ? null : userLoginHistory.getUpdatedAt(),
        progressRate);
  }
}
