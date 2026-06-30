package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.UserLesson;
import com.everrefine.elms.domain.repository.UserLessonRepository;
import com.everrefine.elms.infrastructure.dao.UserLessonDao;
import com.everrefine.elms.infrastructure.dao.UserLessonDao.CompletedLessonCount;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

/** {@link UserLessonRepository} の実装クラス。 */
@Repository
@AllArgsConstructor
public class UserLessonRepositoryImpl implements UserLessonRepository {

  private final UserLessonDao userLessonDao;

  @Override
  public Optional<UserLesson> findByUserIdAndLessonId(Integer userId, Integer lessonId) {
    return userLessonDao.findByUserIdAndLessonId(userId, lessonId);
  }

  @Override
  public void save(UserLesson userLesson) {
    userLessonDao.save(userLesson);
  }

  @Override
  public void deleteByUserIdAndLessonId(Integer userId, Integer lessonId) {
    userLessonDao.deleteByUserIdAndLessonId(userId, lessonId);
  }

  @Override
  public int countAllByUserId(Integer userId) {
    return userLessonDao.countAllByUserId(userId);
  }

  @Override
  public Map<Integer, Integer> countByUserIds(List<Integer> userIds) {
    if (userIds.isEmpty()) {
      return Map.of();
    }
    List<CompletedLessonCount> counts = userLessonDao.countByUserIds(userIds);
    return counts.stream()
        .collect(
            Collectors.toMap(CompletedLessonCount::userId, CompletedLessonCount::completedCount));
  }

  @Override
  public int countCompletedLessonsByUserIdAndCourseId(Integer userId, Integer courseId) {
    return userLessonDao.countCompletedLessonsByUserIdAndCourseId(userId, courseId);
  }

  @Override
  public Set<Integer> findLessonIdByUserIdAndLessonIdIn(Integer userId, Set<Integer> lessonIds) {
    return userLessonDao.findLessonIdByUserIdAndLessonIdIn(userId, lessonIds);
  }
}
