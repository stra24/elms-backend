package com.everrefine.elms.domain.model.user;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@AllArgsConstructor
@Table("user_login_histories")
/** ユーザーログイン履歴のエンティティ。 */
public class UserLoginHistory {

  @Id private final Integer id;

  @Column("user_id")
  private Integer userId;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  /**
   * 新規作成用のログイン履歴を作成する。
   *
   * @param userId ユーザーID
   * @return 新規作成用のログイン履歴
   */
  public static UserLoginHistory create(Integer userId) {
    LocalDateTime now = LocalDateTime.now();
    return new UserLoginHistory(null, userId, now, now);
  }

  /**
   * ログイン日時を現在時刻に更新する。
   *
   * @return 更新されたログイン履歴
   */
  public UserLoginHistory update() {
    return new UserLoginHistory(id, userId, createdAt, LocalDateTime.now());
  }
}
