package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.user.UserSearchCondition;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** ユーザー検索用のコマンド。 */
@Getter
@AllArgsConstructor
public class UserSearchCommand {

  private int pageNum;
  private int pageSize;
  private String userId;
  private String userRole;
  private String realName;
  private String userName;
  private String emailAddress;
  LocalDate createdDateFrom;
  LocalDate createdDateTo;

  /**
   * UserSearchConditionに変換する。
   *
   * @return ユーザー検索条件
   */
  public UserSearchCondition toSearchCondition() {
    return new UserSearchCondition(
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
