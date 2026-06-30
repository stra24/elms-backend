package com.everrefine.elms.infrastructure.repository;

import static com.everrefine.elms.domain.model.Order.getFirst;

import com.everrefine.elms.domain.model.Order;
import com.everrefine.elms.domain.model.PagerForRequest;
import com.everrefine.elms.domain.model.course.Course;
import com.everrefine.elms.domain.repository.CourseRepository;
import com.everrefine.elms.infrastructure.dao.CourseDao;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

/** {@link CourseRepository} の実装クラス。 */
@Repository
@AllArgsConstructor
public class CourseRepositoryImpl implements CourseRepository {

  private final CourseDao courseDao;

  @Override
  public void updateCourse(Course course) {
    courseDao.save(course);
  }

  @Override
  public void createCourse(Course course) {
    courseDao.save(course);
  }

  @Override
  public void deleteCourseById(Integer id) {
    courseDao.deleteById(id);
  }

  @Override
  public Optional<Course> findCourseById(Integer id) {
    return courseDao.findById(id);
  }

  @Override
  public List<Course> findCourses(PagerForRequest pagerForRequest) {
    return courseDao.findCoursesWithPagination(
        pagerForRequest.getPageSize(), pagerForRequest.getOffset());
  }

  @Override
  public int countCourses() {
    return courseDao.countAllCourses();
  }

  @Override
  public Order issueCourseOrder() {
    return courseDao
        .findTop1ByOrderByCourseOrderDesc()
        .map(course -> course.getCourseOrder().getNext())
        .orElse(getFirst());
  }

  @Override
  public List<String> findByThumbnailUrlStartingWith(String prefix) {
    return courseDao.findByThumbnailUrlStartingWith(prefix);
  }
}
