package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.lesson.Lesson;
import com.everrefine.elms.domain.model.lesson.LessonGroupWithLesson;
import com.everrefine.elms.domain.model.lesson.LessonSearchCriteria;
import com.everrefine.elms.domain.model.lesson.LessonWithCourseAndLessonGroup;
import com.everrefine.elms.domain.repository.LessonRepository;
import com.everrefine.elms.infrastructure.dao.LessonDao;
import com.everrefine.elms.infrastructure.dao.LessonGroupDao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

/** {@link LessonRepository} の実装クラス。 */
@Repository
@AllArgsConstructor
public class LessonRepositoryImpl implements LessonRepository {

  private final LessonDao lessonDao;
  private final LessonGroupDao lessonGroupDao;

  @Override
  public Optional<Lesson> findById(Integer lessonId) {
    return lessonDao.findById(lessonId);
  }

  @Override
  public List<Lesson> findByIdIn(List<Integer> lessonIds) {
    if (lessonIds == null || lessonIds.isEmpty()) {
      return List.of();
    }
    return lessonDao.findByIdIn(lessonIds);
  }

  @Override
  public List<Lesson> findLessons(LessonSearchCriteria criteria) {
    Integer courseId =
        criteria.getCourseId() != null ? Integer.valueOf(criteria.getCourseId()) : null;
    Integer lessonGroupId =
        criteria.getLessonGroupId() != null ? Integer.valueOf(criteria.getLessonGroupId()) : null;
    LocalDate createdDateFrom = criteria.getCreatedDateFrom();
    LocalDate createdDateTo = criteria.getCreatedDateTo();

    int offset = (criteria.getPageNum() - 1) * criteria.getPageSize();

    return lessonDao.findLessons(
        courseId,
        lessonGroupId,
        criteria.getTitle(),
        createdDateFrom,
        createdDateTo,
        criteria.getPageSize(),
        offset);
  }

  @Override
  public int countLessons(LessonSearchCriteria criteria) {
    Integer courseId =
        criteria.getCourseId() != null ? Integer.valueOf(criteria.getCourseId()) : null;
    Integer lessonGroupId =
        criteria.getLessonGroupId() != null ? Integer.valueOf(criteria.getLessonGroupId()) : null;
    LocalDate createdDateFrom = criteria.getCreatedDateFrom();
    LocalDate createdDateTo = criteria.getCreatedDateTo();

    return lessonDao.countLessons(
        courseId, lessonGroupId, criteria.getTitle(), createdDateFrom, createdDateTo);
  }

  @Override
  public List<Lesson> findLessonsByLessonGroupId(Integer lessonGroupId) {
    return lessonDao.findLessonsByLessonGroupId(lessonGroupId);
  }

  @Override
  public List<LessonGroupWithLesson> findLessonsGroupedByLessonGroup(Integer courseId) {
    return lessonGroupDao.findLessonGroupsByCourseId(courseId);
  }

  @Override
  public Lesson createLesson(Lesson lesson) {
    return lessonDao.save(lesson);
  }

  @Override
  public Lesson updateLesson(Lesson lesson) {
    return lessonDao.save(lesson);
  }

  @Override
  public Optional<BigDecimal> findMaxLessonOrderByLessonGroupId(Integer lessonGroupId) {
    return lessonDao.findMaxLessonOrderByLessonGroupId(lessonGroupId);
  }

  @Override
  public void deleteLessonById(Integer lessonId) {
    lessonDao.deleteById(lessonId);
  }

  @Override
  public int countAllLessons() {
    return lessonDao.countAllLessons();
  }

  @Override
  public List<String> findByVideoUrlStartingWith(String prefix) {
    return lessonDao.findByVideoUrlStartingWith(prefix);
  }

  @Override
  public List<LessonWithCourseAndLessonGroup> findAllLessons() {
    return lessonDao.findByAllLessons();
  }
}
