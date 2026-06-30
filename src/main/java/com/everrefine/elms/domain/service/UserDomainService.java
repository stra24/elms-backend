package com.everrefine.elms.domain.service;

import com.everrefine.elms.domain.model.user.User;

/** ユーザーに関するドメインサービスインターフェース。 */
public interface UserDomainService {

  /**
   * パスワードと確認用パスワードが合致するかを判定する。
   *
   * @param password パスワード
   * @param confirmPassword 確認用パスワード
   * @return ture: 合致する, false: 合致しない
   */
  static boolean matchesPassword(String password, String confirmPassword) {
    return password != null && password.equals(confirmPassword);
  }

  /**
   * ログイン中のユーザーを取得する。
   *
   * @return ログイン中のユーザー
   */
  User getLoginUser();
}
