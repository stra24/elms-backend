package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.user.EmailAddress;
import com.everrefine.elms.domain.model.user.User;
import com.everrefine.elms.domain.model.user.UserSearchCondition;
import com.everrefine.elms.domain.repository.UserRepository;
import com.everrefine.elms.infrastructure.dao.UserDao;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final UserDao userDao;
  private final JdbcTemplate jdbcTemplate;

  @Override
  public List<User> findUsersByIds(List<Integer> userIds) {
    if (userIds.isEmpty()) {
      return Collections.emptyList();
    }
    return userDao.findByIdIn(userIds);
  }

  @Override
  public User updateUser(User user) {
    return userDao.save(user);
  }

  @Override
  public User createUser(User user) {
    return userDao.save(user);
  }

  @Override
  public void deleteUserById(Integer id) {
    userDao.deleteById(id);
  }

  @Override
  public Optional<User> findUserById(Integer id) {
    return userDao.findById(id);
  }

  @Override
  public Optional<User> findUserByEmailAddress(EmailAddress emailAddress) {
    return userDao.findByEmailAddress(emailAddress.getValue());
  }

  @Override
  public List<Integer> findUserIdsBySearchConditions(UserSearchCondition userSearchCondition) {
    return userDao.findUserIdsBySearchConditions(
        userSearchCondition.getUserId(),
        userSearchCondition.getUserRole(),
        userSearchCondition.getRealName(),
        userSearchCondition.getUserName(),
        userSearchCondition.getEmailAddress(),
        userSearchCondition.getCreatedDateFrom() == null
            ? null
            : userSearchCondition.getCreatedDateFrom(),
        userSearchCondition.getCreatedDateTo() == null
            ? null
            : userSearchCondition.getCreatedDateTo(),
        userSearchCondition.getPageSize(),
        userSearchCondition.getOffset());
  }

  @Override
  public int countUsers(UserSearchCondition userSearchCondition) {
    return userDao.countUsersBySearchConditions(
        userSearchCondition.getUserId(),
        userSearchCondition.getUserRole(),
        userSearchCondition.getRealName(),
        userSearchCondition.getUserName(),
        userSearchCondition.getEmailAddress(),
        userSearchCondition.getCreatedDateFrom() == null
            ? null
            : userSearchCondition.getCreatedDateFrom(),
        userSearchCondition.getCreatedDateTo() == null
            ? null
            : userSearchCondition.getCreatedDateTo());
  }

  @Override
  public List<User> findAllByOrderByIdAsc() {
    return userDao.findAllByOrderByIdAsc();
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
    List<User> usersWithIds = users.stream().filter(user -> user.getId() != null).toList();
    List<User> usersWithoutIds = users.stream().filter(user -> user.getId() == null).toList();

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
          ps.setInt(1, user.getId());
          ps.setString(2, user.getEmailAddress().getValue());
          ps.setString(3, user.getPassword().getValue());
          ps.setString(4, user.getRealName().getValue());
          ps.setString(5, user.getUserName().getValue());
          ps.setString(
              6, user.getThumbnailUrl() != null ? user.getThumbnailUrl().getValue() : null);
          ps.setString(7, user.getUserRole().name());
          ps.setObject(8, user.getCreatedAt());
          ps.setObject(9, user.getUpdatedAt());
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
          ps.setString(1, user.getEmailAddress().getValue());
          ps.setString(2, user.getPassword().getValue());
          ps.setString(3, user.getRealName().getValue());
          ps.setString(4, user.getUserName().getValue());
          ps.setString(
              5, user.getThumbnailUrl() != null ? user.getThumbnailUrl().getValue() : null);
          ps.setString(6, user.getUserRole().name());
          ps.setObject(7, user.getCreatedAt());
          ps.setObject(8, user.getUpdatedAt());
        });
  }
}
