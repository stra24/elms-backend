package com.everrefine.elms.application.service;

import com.everrefine.elms.application.command.UserLessonCompletionStatusUpdateCommand;
import com.everrefine.elms.application.dto.LessonDto;
import com.everrefine.elms.application.dto.UserLessonDetailDto;
import com.everrefine.elms.application.dto.UserLessonDto;
import com.everrefine.elms.application.dto.UserLessonGroupDto;
import com.everrefine.elms.application.exception.ResourceNotFoundException;
import com.everrefine.elms.domain.model.UserLesson;
import com.everrefine.elms.domain.model.course.Course;
import com.everrefine.elms.domain.model.lesson.Lesson;
import com.everrefine.elms.domain.model.lesson.LessonGroupWithLesson;
import com.everrefine.elms.domain.model.user.User;
import com.everrefine.elms.domain.repository.CourseRepository;
import com.everrefine.elms.domain.repository.LessonRepository;
import com.everrefine.elms.domain.repository.UserLessonRepository;
import com.everrefine.elms.domain.repository.UserRepository;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** ユーザーレッスンアプリケーションサービスの実装に関するクラス。 */
@Service
@AllArgsConstructor
public class UserLessonApplicationServiceImpl implements UserLessonApplicationService {

  private final LessonRepository lessonRepository;
  private final UserRepository userRepository;
  private final UserLessonRepository userLessonRepository;
  private final CourseRepository courseRepository;

  @Override
  @Transactional(readOnly = true)
  public UserLessonDetailDto findUserLessonDetail(
      Integer userId, Integer courseId, Integer lessonGroupId, Integer lessonId) {
    Lesson lesson =
        findLessonBelongingToCourseAndLessonGroupOrThrow(lessonId, courseId, lessonGroupId);
    boolean isLessonCompleted =
        userLessonRepository.findByUserIdAndLessonId(userId, lessonId).isPresent();
    return UserLessonDetailDto.from(lesson, isLessonCompleted);
  }

  @Override
  @Transactional
  public void updateUserLesson(
      Integer courseId,
      Integer lessonGroupId,
      UserLessonCompletionStatusUpdateCommand userLessonCompletionStatusUpdateCommand) {
    // データが存在することのチェックする。
    findLessonBelongingToCourseAndLessonGroupOrThrow(
        userLessonCompletionStatusUpdateCommand.getLessonId(), courseId, lessonGroupId);
    userRepository
        .findUserById(userLessonCompletionStatusUpdateCommand.getUserId())
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    User.class, userLessonCompletionStatusUpdateCommand.getUserId().toString()));

    // ユーザーのレッスン完了状態を更新する。
    if (userLessonCompletionStatusUpdateCommand.isLessonCompleted()) {
      userLessonRepository
          .findByUserIdAndLessonId(
              userLessonCompletionStatusUpdateCommand.getUserId(),
              userLessonCompletionStatusUpdateCommand.getLessonId())
          .ifPresentOrElse(
              currentUserLesson -> {
                UserLesson updatedUserLesson = currentUserLesson.update();
                userLessonRepository.save(updatedUserLesson);
              },
              () -> {
                UserLesson createdUserLesson =
                    userLessonCompletionStatusUpdateCommand.toNewUserLesson();
                userLessonRepository.save(createdUserLesson);
              });
    } else {
      userLessonRepository.deleteByUserIdAndLessonId(
          userLessonCompletionStatusUpdateCommand.getUserId(),
          userLessonCompletionStatusUpdateCommand.getLessonId());
    }
  }

  /**
   * コースとレッスングループに属するレッスンを取得する。存在しない場合は例外をスローする。
   *
   * @param lessonId レッスンID
   * @param courseId コースID
   * @param lessonGroupId レッスングループID
   * @return レッスンエンティティ
   */
  private Lesson findLessonBelongingToCourseAndLessonGroupOrThrow(
      Integer lessonId, Integer courseId, Integer lessonGroupId) {
    Lesson lesson = findLessonOrThrow(lessonId);
    if (!lesson.getCourseId().equals(courseId)
        || !lesson.getLessonGroupId().equals(lessonGroupId)) {
      throw new ResourceNotFoundException(Lesson.class, String.valueOf(lessonId));
    }
    return lesson;
  }

  /**
   * IDでレッスンを取得する。存在しない場合は例外をスローする。
   *
   * @param lessonId レッスンID
   * @return レッスンエンティティ
   */
  private Lesson findLessonOrThrow(Integer lessonId) {
    return lessonRepository
        .findById(lessonId)
        .orElseThrow(() -> new ResourceNotFoundException(Lesson.class, String.valueOf(lessonId)));
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserLessonGroupDto> findUserLessons(Integer userId, Integer courseId) {
    userRepository
        .findUserById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(User.class, String.valueOf(userId)));
    courseRepository
        .findCourseById(courseId)
        .orElseThrow(() -> new ResourceNotFoundException(Course.class, String.valueOf(courseId)));

    List<LessonGroupWithLesson> allLessonsInTargetCourse =
        lessonRepository.findLessonsGroupedByLessonGroup(courseId);

    Set<Integer> lessonIds =
        allLessonsInTargetCourse.stream()
            .map(LessonGroupWithLesson::getLessonId)
            .collect(Collectors.toSet());

    Set<Integer> completedLessonIds =
        userLessonRepository.findLessonIdByUserIdAndLessonIdIn(userId, lessonIds);

    Map<Integer, List<LessonGroupWithLesson>> lessonGroupIdAndLessonsMap =
        allLessonsInTargetCourse.stream()
            .sorted(Comparator.comparing(LessonGroupWithLesson::getLessonGroupOrder))
            .collect(
                Collectors.groupingBy(
                    LessonGroupWithLesson::getLessonGroupId,
                    LinkedHashMap::new,
                    Collectors.toList()));
    return lessonGroupIdAndLessonsMap.values().stream()
        .map(
            allLessonsInTargetLessonGroup -> {
              List<UserLessonDto> userLessonDtos =
                  allLessonsInTargetLessonGroup.stream()
                      .sorted(Comparator.comparing(LessonGroupWithLesson::getLessonOrder))
                      .map(
                          lesson ->
                              new UserLessonDto(
                                  LessonDto.from(lesson),
                                  completedLessonIds.contains(lesson.getLessonId())))
                      .toList();
              return UserLessonGroupDto.from(allLessonsInTargetLessonGroup, userLessonDtos);
            })
        .toList();
  }
}
