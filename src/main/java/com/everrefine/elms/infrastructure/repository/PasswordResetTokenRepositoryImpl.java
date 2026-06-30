package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.passwordreset.PasswordResetToken;
import com.everrefine.elms.domain.repository.PasswordResetTokenRepository;
import com.everrefine.elms.infrastructure.dao.PasswordResetTokenDao;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

/** {@link PasswordResetTokenRepository} の実装クラス。 */
@Repository
@AllArgsConstructor
public class PasswordResetTokenRepositoryImpl implements PasswordResetTokenRepository {

  private final PasswordResetTokenDao dao;

  @Override
  public PasswordResetToken save(PasswordResetToken token) {
    return dao.save(token);
  }

  @Override
  public Optional<PasswordResetToken> findByToken(String token) {
    return dao.findByToken(token);
  }
}
