package com.everrefine.elms.domain.service;

import com.everrefine.elms.domain.model.user.User;
import com.everrefine.elms.domain.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/** {@link UserDomainService} の実装。 */
@Component
@RequiredArgsConstructor
public class UserDomainServiceImpl implements UserDomainService {

  private final UserRepository userRepository;

  /**
   * ログインユーザーをUser型のインスタンスとして取得します。
   *
   * @return ログインユーザー
   */
  @Override
  public User getLoginUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    UUID userId = UUID.fromString(userDetails.getUsername());
    return userRepository
        .findUserById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }
}
