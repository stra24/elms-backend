package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.lesson.Lesson;
import com.everrefine.elms.domain.model.lesson.LessonGroupWithLessons;
import com.everrefine.elms.domain.model.lesson.LessonSearchCriteria;
import com.everrefine.elms.domain.model.lesson.LessonWithCourseAndLessonGroup;
import com.everrefine.elms.domain.repository.LessonRepository;
import com.everrefine.elms.infrastructure.dao.LessonDao;
import com.everrefine.elms.infrastructure.dao.LessonGroupDao;
import com.everrefine.elms.infrastructure.entity.lesson.LessonEntity;
import com.everrefine.elms.infrastructure.row.LessonGroupWithLessonRow;
import com.everrefine.elms.infrastructure.row.LessonWithCourseAndLessonGroupRow;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** {@link LessonRepository} の実装。 */
@Repository
@AllArgsConstructor
public class LessonRepositoryImpl implements LessonRepository {

  private final LessonDao lessonDao;
  private final LessonGroupDao lessonGroupDao;
  private final JdbcTemplate jdbcTemplate;

  @Override
  public Optional<Lesson> findById(UUID lessonId) {
    return lessonDao.findById(lessonId).map(LessonEntity::toDomain);
  }

  @Override
  public List<Lesson> findByIdIn(List<UUID> lessonIds) {
    if (lessonIds == null || lessonIds.isEmpty()) {
      return List.of();
    }
    return lessonDao.findByIdIn(lessonIds).stream().map(LessonEntity::toDomain).toList();
  }

  @Override
  public List<Lesson> findLessons(LessonSearchCriteria criteria) {
    UUID courseId = criteria.courseId() != null ? UUID.fromString(criteria.courseId()) : null;
    UUID lessonGroupId =
        criteria.lessonGroupId() != null ? UUID.fromString(criteria.lessonGroupId()) : null;
    LocalDate createdDateFrom = criteria.createdDateFrom();
    LocalDate createdDateTo = criteria.createdDateTo();

    return lessonDao
        .findLessons(
            courseId,
            lessonGroupId,
            criteria.title(),
            createdDateFrom,
            createdDateTo,
            criteria.getPageSize(),
            criteria.getOffset())
        .stream()
        .map(LessonEntity::toDomain)
        .toList();
  }

  @Override
  public int countLessons(LessonSearchCriteria criteria) {
    UUID courseId = criteria.courseId() != null ? UUID.fromString(criteria.courseId()) : null;
    UUID lessonGroupId =
        criteria.lessonGroupId() != null ? UUID.fromString(criteria.lessonGroupId()) : null;
    LocalDate createdDateFrom = criteria.createdDateFrom();
    LocalDate createdDateTo = criteria.createdDateTo();

    return lessonDao.countLessons(
        courseId, lessonGroupId, criteria.title(), createdDateFrom, createdDateTo);
  }

  @Override
  public List<Lesson> findLessonsByLessonGroupId(UUID lessonGroupId) {
    return lessonDao.findLessonsByLessonGroupId(lessonGroupId).stream()
        .map(LessonEntity::toDomain)
        .toList();
  }

  @Override
  public List<LessonGroupWithLessons> findLessonsGroupedByLessonGroup(UUID courseId) {
    return LessonGroupWithLessonRow.toDomainList(
        lessonGroupDao.findLessonGroupsByCourseId(courseId));
  }

  @Override
  public Lesson createLesson(Lesson lesson) {
    return lessonDao.save(LessonEntity.from(lesson)).toDomain();
  }

  @Override
  public void createLessons(List<Lesson> lessons) {
    if (lessons.isEmpty()) {
      return;
    }

    jdbcTemplate.batchUpdate(
        """
            INSERT INTO lessons (lesson_group_id, course_id, lesson_order, title, content, video_url, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """,
        lessons,
        lessons.size(),
        (ps, lesson) -> {
          ps.setObject(1, lesson.lessonGroupId());
          ps.setObject(2, lesson.courseId());
          ps.setBigDecimal(3, lesson.lessonOrder().value());
          ps.setString(4, lesson.title().value());
          ps.setString(5, lesson.content() != null ? lesson.content().value() : null);
          ps.setString(6, lesson.videoUrl() != null ? lesson.videoUrl().value() : null);
          ps.setObject(7, lesson.createdAt());
          ps.setObject(8, lesson.updatedAt());
        });
  }

  @Override
  public Lesson updateLesson(Lesson lesson) {
    return lessonDao.save(LessonEntity.from(lesson)).toDomain();
  }

  @Override
  public Optional<BigDecimal> findMaxLessonOrderByLessonGroupId(UUID lessonGroupId) {
    return lessonDao.findMaxLessonOrderByLessonGroupId(lessonGroupId);
  }

  @Override
  public void deleteLessonById(UUID lessonId) {
    lessonDao.deleteById(lessonId);
  }

  @Override
  public void deleteLessonsByCourseId(UUID courseId) {
    lessonDao.deleteByCourseId(courseId);
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
    return lessonDao.findByAllLessons().stream()
        .map(LessonWithCourseAndLessonGroupRow::toDomain)
        .toList();
  }
}
