package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.lesson.LessonGroup;
import com.everrefine.elms.domain.repository.LessonGroupRepository;
import com.everrefine.elms.infrastructure.dao.LessonGroupDao;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

/** {@link LessonGroupRepository} の実装クラス。 */
@Repository
@AllArgsConstructor
public class LessonGroupRepositoryImpl implements LessonGroupRepository {

  private final LessonGroupDao lessonGroupDao;

  @Override
  public LessonGroup createLessonGroup(LessonGroup lessonGroup) {
    return lessonGroupDao.save(lessonGroup);
  }

  @Override
  public LessonGroup updateLessonGroup(LessonGroup lessonGroup) {
    return lessonGroupDao.save(lessonGroup);
  }

  @Override
  public Optional<BigDecimal> findMaxLessonGroupOrderByCourseId(Integer courseId) {
    return lessonGroupDao.findMaxLessonGroupOrderByCourseId(courseId);
  }

  @Override
  public Optional<LessonGroup> findLessonGroupById(Integer id) {
    return lessonGroupDao.findById(id);
  }

  @Override
  public void deleteLessonGroupById(Integer id) {
    lessonGroupDao.deleteById(id);
  }
}
