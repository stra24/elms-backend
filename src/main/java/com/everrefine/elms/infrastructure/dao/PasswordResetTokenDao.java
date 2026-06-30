package com.everrefine.elms.infrastructure.dao;

import com.everrefine.elms.domain.model.passwordreset.PasswordResetToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** パスワードリセットトークンのDAOインターフェース。 */
@Repository
public interface PasswordResetTokenDao extends CrudRepository<PasswordResetToken, Integer> {

  /**
   * トークン文字列でパスワードリセットトークンを取得する。
   *
   * @param token トークン文字列
   * @return パスワードリセットトークン（存在しない場合は空）
   */
  Optional<PasswordResetToken> findByToken(String token);
}
