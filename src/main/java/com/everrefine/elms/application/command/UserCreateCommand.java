package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.user.User;
import com.everrefine.elms.domain.model.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** 新規作成用ユーザーのコマンド。 */
@Getter
@AllArgsConstructor
public class UserCreateCommand {

  private Integer id;
  private String realName;
  private String userName;
  private String emailAddress;
  private String password;
  private String confirmPassword;
  private String thumbnailUrl;
  private UserRole userRole;

  /**
   * Userエンティティに変換する。
   *
   * @return ユーザーエンティティ
   */
  public User toUser() {
    return User.create(emailAddress, password, realName, userName, thumbnailUrl, userRole);
  }
}
