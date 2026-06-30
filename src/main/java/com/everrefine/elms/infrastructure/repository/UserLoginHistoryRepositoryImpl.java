package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.user.UserLoginHistory;
import com.everrefine.elms.domain.repository.UserLoginHistoryRepository;
import com.everrefine.elms.infrastructure.dao.UserLoginHistoryDao;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

/** {@link UserLoginHistoryRepository} の実装クラス。 */
@Repository
@AllArgsConstructor
public class UserLoginHistoryRepositoryImpl implements UserLoginHistoryRepository {

  private final UserLoginHistoryDao userLoginHistoryDao;

  @Override
  public Optional<UserLoginHistory> findUserLoginHistoryByUserId(Integer userId) {
    return userLoginHistoryDao.findByUserId(userId);
  }

  @Override
  public List<UserLoginHistory> findByUserIds(List<Integer> userIds) {
    if (userIds.isEmpty()) {
      return Collections.emptyList();
    }
    return userLoginHistoryDao.findByUserIds(userIds);
  }

  @Override
  public Map<Integer, UserLoginHistory> findByUserIdsAsMap(List<Integer> userIds) {
    if (userIds.isEmpty()) {
      return Collections.emptyMap();
    }
    return userLoginHistoryDao.findByUserIds(userIds).stream()
        .collect(Collectors.toMap(UserLoginHistory::getUserId, history -> history, (h1, h2) -> h1));
  }

  @Override
  public void save(UserLoginHistory userLoginHistory) {
    userLoginHistoryDao.save(userLoginHistory);
  }
}
