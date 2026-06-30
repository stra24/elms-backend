package com.everrefine.elms.infrastructure.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/** Spring Security設定に関するクラス。 */
@EnableMethodSecurity
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtFilter jwtFilter;

  @Value("${BASE_URL:http://localhost:3000}")
  private String baseUrl;

  /**
   * SecurityConfigのコンストラクタ。
   *
   * @param jwtFilter JWTフィルター
   */
  public SecurityConfig(JwtFilter jwtFilter) {
    this.jwtFilter = jwtFilter;
  }

  /**
   * パスワードエンコーダーを生成する。
   *
   * @return BCryptパスワードエンコーダー
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * セキュリティフィルターチェーンを生成する。
   *
   * @param http HttpSecurity
   * @return セキュリティフィルターチェーン
   * @throws Exception 設定例外
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable) // JWTをリクエストヘッダーに付与して送る運用のため、CSRFトークンの検証は不要。
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // セッションを無効にする
            )
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/api/password-reset/**",
                        "/uploads/**",
                        "/actuator/health",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**")
                    .permitAll() // 特定のリクエストは認証なしで許可する
                    .anyRequest()
                    .authenticated() // それ以外の全てのリクエストは認証が必要とする
            )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // JWT認証フィルターを追加

    return http.build();
  }

  /**
   * 認証マネージャーを生成する。
   *
   * @param config 認証設定
   * @return 認証マネージャー
   * @throws Exception 設定例外
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  /**
   * CORSの設定ソースを生成する。
   *
   * @return CORSの設定ソース
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000", baseUrl));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true); // ← クッキーを使うなら true

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
  }
}
