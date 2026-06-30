package com.everrefine.elms.domain.model.user;

import com.everrefine.elms.domain.model.PagerForRequest;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import lombok.Value;

/** ユーザー検索条件の値オブジェクト。 */
@Value
public class UserSearchCondition {

  /** リクエスト用のページャー情報 */
  PagerForRequest pagerForRequest;

  /** ユーザーID */
  @Nullable String userId;

  /** 権限 */
  @Nullable String userRole;

  /** 氏名 */
  @Nullable String realName;

  /** ユーザー名 */
  @Nullable String userName;

  /** メールアドレス */
  @Nullable String emailAddress;

  /** 作成日From */
  @Nullable LocalDate createdDateFrom;

  /** 作成日To */
  @Nullable LocalDate createdDateTo;

  /**
   * ユーザー検索条件を作成する。
   *
   * @param pageNum ページ番号
   * @param pageSize 1ページ当たりの件数
   * @param userId ユーザーID（nullの場合は絞り込みなし）
   * @param userRole 権限（nullの場合は絞り込みなし）
   * @param realName 氏名（nullの場合は絞り込みなし）
   * @param userName ユーザー名（nullの場合は絞り込みなし）
   * @param emailAddress メールアドレス（nullの場合は絞り込みなし）
   * @param createdDateFrom 作成日From（nullの場合は絞り込みなし）
   * @param createdDateTo 作成日To（nullの場合は絞り込みなし）
   */
  public UserSearchCondition(
      int pageNum,
      int pageSize,
      String userId,
      String userRole,
      String realName,
      String userName,
      String emailAddress,
      LocalDate createdDateFrom,
      LocalDate createdDateTo) {
    this.pagerForRequest = new PagerForRequest(pageNum, pageSize);
    this.userId = userId;
    this.userRole = userRole;
    this.realName = realName;
    this.userName = userName;
    this.emailAddress = emailAddress;
    this.createdDateFrom = createdDateFrom;
    this.createdDateTo = createdDateTo;
  }

  /**
   * 1ページ当たりの件数を返す。
   *
   * @return 1ページ当たりの件数
   */
  public int getPageSize() {
    return pagerForRequest.getPageSize();
  }

  /**
   * DBクエリ用のオフセット値を返す。
   *
   * @return オフセット値
   */
  public int getOffset() {
    return pagerForRequest.getOffset();
  }
}
