package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.user.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** 更新用ユーザーのコマンド。 */
@Getter
@AllArgsConstructor
public class UserUpdateCommand {

  private Integer id;
  private String realName;
  private String userName;
  private String emailAddress;
  private String thumbnailUrl;
  private LocalDateTime updatedAt;

  /**
   * Userエンティティに変換する。
   *
   * @param user 更新対象のユーザー
   * @return 更新後のユーザーエンティティ
   */
  public User toUser(User user) {
    return user.update(emailAddress, null, realName, userName, thumbnailUrl, null);
  }
}
