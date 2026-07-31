package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.lesson.LessonGroup;
import com.everrefine.elms.domain.repository.LessonGroupRepository;
import com.everrefine.elms.infrastructure.dao.LessonGroupDao;
import com.everrefine.elms.infrastructure.entity.lesson.LessonGroupEntity;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

/** {@link LessonGroupRepository} の実装。 */
@Repository
@AllArgsConstructor
public class LessonGroupRepositoryImpl implements LessonGroupRepository {

  private final LessonGroupDao lessonGroupDao;

  @Override
  public LessonGroup createLessonGroup(LessonGroup lessonGroup) {
    return lessonGroupDao.save(LessonGroupEntity.from(lessonGroup)).toDomain();
  }

  @Override
  public LessonGroup updateLessonGroup(LessonGroup lessonGroup) {
    return lessonGroupDao.save(LessonGroupEntity.from(lessonGroup)).toDomain();
  }

  @Override
  public Optional<BigDecimal> findMaxLessonGroupOrderByCourseId(UUID courseId) {
    return lessonGroupDao.findMaxLessonGroupOrderByCourseId(courseId);
  }

  @Override
  public Optional<LessonGroup> findLessonGroupById(UUID id) {
    return lessonGroupDao.findById(id).map(LessonGroupEntity::toDomain);
  }

  @Override
  public void deleteLessonGroupById(UUID id) {
    lessonGroupDao.deleteById(id);
  }

  @Override
  public void deleteLessonGroupsByCourseId(UUID courseId) {
    lessonGroupDao.deleteByCourseId(courseId);
  }
}
