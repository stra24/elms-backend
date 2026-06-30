package com.everrefine.elms.domain.model.user;

import com.everrefine.elms.domain.model.Url;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

/** ユーザーのエンティティ。 */
@Getter
@AllArgsConstructor
@Table("users")
public class User {

  @Id private final Integer id;

  @Column("email_address")
  private EmailAddress emailAddress;

  private Password password;

  @Column("real_name")
  private RealName realName;

  @Column("user_name")
  private UserName userName;

  @Nullable @Column("thumbnail_url")
  private Url thumbnailUrl;

  @Column("user_role")
  private UserRole userRole;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  /**
   * 新規作成用のユーザーを作成する。
   *
   * @param emailAddress メールアドレス
   * @param password パスワード
   * @param realName 本名
   * @param userName ユーザー名
   * @param thumbnailUrl サムネイル画像のURL
   * @param userRole 権限
   * @return 新規作成用のユーザー
   */
  public static User create(
      String emailAddress,
      String password,
      String realName,
      String userName,
      String thumbnailUrl,
      UserRole userRole) {
    LocalDateTime now = LocalDateTime.now();
    return new User(
        null,
        new EmailAddress(emailAddress),
        Password.encryptAndCreate(password),
        new RealName(realName),
        new UserName(userName),
        thumbnailUrl == null ? null : new Url(thumbnailUrl),
        userRole,
        now,
        now);
  }

  /**
   * 更新用のユーザーを作成する。
   *
   * @param emailAddress メールアドレス
   * @param password パスワード
   * @param realName 本名
   * @param userName ユーザー名
   * @param thumbnailUrl サムネイル画像のURL
   * @param userRole 権限
   * @return 更新用のユーザー
   */
  public User update(
      String emailAddress,
      String password,
      String realName,
      String userName,
      String thumbnailUrl,
      UserRole userRole) {
    return new User(
        this.id,
        emailAddress == null ? this.emailAddress : new EmailAddress(emailAddress),
        password == null ? this.password : Password.encryptAndCreate(password),
        realName == null ? this.realName : new RealName(realName),
        userName == null ? this.userName : new UserName(userName),
        thumbnailUrl == null ? this.thumbnailUrl : new Url(thumbnailUrl),
        userRole == null ? this.userRole : userRole,
        this.createdAt,
        LocalDateTime.now());
  }

  /**
   * 該当のパスワードと自身のパスワードが一致するかを判定します。
   *
   * @param currentPassword 該当のパスワード
   * @return 一致する場合はtrue,不一致の場合はfalse
   */
  public boolean isCurrentPasswordMatch(String currentPassword) {
    return this.password.matches(currentPassword);
  }
}
