package com.everrefine.elms.infrastructure.security;

import com.everrefine.elms.domain.model.user.EmailAddress;
import com.everrefine.elms.domain.model.user.User;
import com.everrefine.elms.domain.repository.UserRepository;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Spring Security用のユーザー詳細サービスに関するクラス。 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * CustomUserDetailsServiceのコンストラクタ。
   *
   * @param repo ユーザーリポジトリ
   */
  public CustomUserDetailsService(UserRepository repo) {
    this.userRepository = repo;
  }

  @Override
  public UserDetails loadUserByUsername(String emailAddress) {
    User user =
        userRepository
            .findUserByEmailAddress(new EmailAddress(emailAddress))
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return new org.springframework.security.core.userdetails.User(
        user.getId().toString(),
        user.getPassword().getValue(),
        List.of(new SimpleGrantedAuthority(user.getUserRole().getCode())));
  }

  /**
   * ユーザーIDでユーザー詳細を取得する。
   *
   * @param userId ユーザーID
   * @return ユーザー詳細
   * @throws UsernameNotFoundException ユーザーが存在しない場合
   */
  public UserDetails loadUserById(String userId) {
    try {
      User user =
          userRepository
              .findUserById(Integer.valueOf(userId))
              .orElseThrow(() -> new UsernameNotFoundException("User not found"));

      return new org.springframework.security.core.userdetails.User(
          user.getId().toString(),
          user.getPassword().getValue(),
          List.of(new SimpleGrantedAuthority(user.getUserRole().getCode())));
    } catch (UsernameNotFoundException e) {
      throw e;
    } catch (Exception e) {
      // DBデータ不正（パスワードフォーマット破損など）はUsernameNotFoundExceptionに変換し、
      // JwtFilterのtry-catchで適切に処理させる
      throw new UsernameNotFoundException("Failed to load user: " + userId, e);
    }
  }
}
