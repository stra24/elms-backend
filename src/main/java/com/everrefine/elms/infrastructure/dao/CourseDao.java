package com.everrefine.elms.infrastructure.dao;

import com.everrefine.elms.domain.model.course.Course;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** コースのDAOインターフェース。 */
@Repository
public interface CourseDao extends CrudRepository<Course, Integer> {

  @Query(
      """
            SELECT *
            FROM courses
            ORDER BY course_order ASC
            LIMIT :pageSize
            OFFSET :offset
          """)
  List<Course> findCoursesWithPagination(
      @Param("pageSize") int pageSize, @Param("offset") int offset);

  @Query(
      """
        SELECT COUNT(*)
        FROM courses
      """)
  int countAllCourses();

  Optional<Course> findTop1ByOrderByCourseOrderDesc();

  @Query(
      """
          SELECT thumbnail_url
          FROM courses
          WHERE thumbnail_url LIKE CONCAT(:prefix, '%')
          """)
  List<String> findByThumbnailUrlStartingWith(String prefix);
}
