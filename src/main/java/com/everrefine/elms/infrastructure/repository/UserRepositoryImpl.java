package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.user.EmailAddress;
import com.everrefine.elms.domain.model.user.User;
import com.everrefine.elms.domain.model.user.UserSearchCondition;
import com.everrefine.elms.domain.repository.UserRepository;
import com.everrefine.elms.infrastructure.dao.UserDao;
import com.everrefine.elms.infrastructure.entity.user.UserEntity;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final UserDao userDao;
  private final JdbcTemplate jdbcTemplate;

  @Override
  public List<User> findUsersByIds(List<UUID> userIds) {
    if (userIds.isEmpty()) {
      return Collections.emptyList();
    }
    return userDao.findByIdIn(userIds).stream().map(UserEntity::toDomain).toList();
  }

  @Override
  public User updateUser(User user) {
    return userDao.save(UserEntity.from(user)).toDomain();
  }

  @Override
  public User createUser(User user) {
    return userDao.save(UserEntity.from(user)).toDomain();
  }

  @Override
  public void deleteUserById(UUID id) {
    userDao.deleteById(id);
  }

  @Override
  public Optional<User> findUserById(UUID id) {
    return userDao.findById(id).map(UserEntity::toDomain);
  }

  @Override
  public Optional<User> findUserByEmailAddress(EmailAddress emailAddress) {
    return userDao.findByEmailAddress(emailAddress.value()).map(UserEntity::toDomain);
  }

  @Override
  public List<UUID> findUserIdsBySearchConditions(UserSearchCondition userSearchCondition) {
    return userDao.findUserIdsBySearchConditions(
        userSearchCondition.userId(),
        userSearchCondition.userRole(),
        userSearchCondition.realName(),
        userSearchCondition.userName(),
        userSearchCondition.emailAddress(),
        userSearchCondition.createdDateFrom() == null
            ? null
            : userSearchCondition.createdDateFrom(),
        userSearchCondition.createdDateTo() == null ? null : userSearchCondition.createdDateTo(),
        userSearchCondition.getPageSize(),
        userSearchCondition.getOffset());
  }

  @Override
  public int countUsers(UserSearchCondition userSearchCondition) {
    return userDao.countUsersBySearchConditions(
        userSearchCondition.userId(),
        userSearchCondition.userRole(),
        userSearchCondition.realName(),
        userSearchCondition.userName(),
        userSearchCondition.emailAddress(),
        userSearchCondition.createdDateFrom() == null
            ? null
            : userSearchCondition.createdDateFrom(),
        userSearchCondition.createdDateTo() == null ? null : userSearchCondition.createdDateTo());
  }

  @Override
  public List<User> findAllByOrderByCreatedAtAscIdAsc() {
    return userDao.findAllByOrderByCreatedAtAscIdAsc().stream().map(UserEntity::toDomain).toList();
  }

  @Override
  public List<String> findByThumbnailUrlStartingWith(String prefix) {
    return userDao.findByThumbnailUrlStartingWith(prefix);
  }

  @Override
  public void deleteAllUsers() {
    userDao.deleteAll();
  }

  /**
   * 複数のユーザーを一括登録する。
   *
   * @param users 登録するユーザーリスト
   * @return 登録されたユーザーリスト
   */
  @Override
  public List<User> saveAllUsers(List<User> users) {
    List<User> usersWithIds = users.stream().filter(user -> user.id() != null).toList();
    List<User> usersWithoutIds = users.stream().filter(user -> user.id() == null).toList();

    insertUsersWithIds(usersWithIds);
    insertUsersWithoutIds(usersWithoutIds);

    return users;
  }

  private void insertUsersWithIds(List<User> users) {
    if (users.isEmpty()) {
      return;
    }

    jdbcTemplate.batchUpdate(
        """
            INSERT INTO users (id, email_address, password, real_name, user_name, thumbnail_url, user_role, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
        users,
        users.size(),
        (ps, user) -> {
          ps.setObject(1, user.id());
          ps.setString(2, user.emailAddress().value());
          ps.setString(3, user.password().value());
          ps.setString(4, user.realName().value());
          ps.setString(5, user.userName().value());
          ps.setString(6, user.thumbnailUrl() != null ? user.thumbnailUrl().value() : null);
          ps.setString(7, user.userRole().name());
          ps.setObject(8, user.createdAt());
          ps.setObject(9, user.updatedAt());
        });
  }

  private void insertUsersWithoutIds(List<User> users) {
    if (users.isEmpty()) {
      return;
    }

    jdbcTemplate.batchUpdate(
        """
            INSERT INTO users (email_address, password, real_name, user_name, thumbnail_url, user_role, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """,
        users,
        users.size(),
        (ps, user) -> {
          ps.setString(1, user.emailAddress().value());
          ps.setString(2, user.password().value());
          ps.setString(3, user.realName().value());
          ps.setString(4, user.userName().value());
          ps.setString(5, user.thumbnailUrl() != null ? user.thumbnailUrl().value() : null);
          ps.setString(6, user.userRole().name());
          ps.setObject(7, user.createdAt());
          ps.setObject(8, user.updatedAt());
        });
  }
}
