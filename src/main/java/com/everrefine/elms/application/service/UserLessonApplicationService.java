package com.everrefine.elms.application.service;

import com.everrefine.elms.application.command.UserLessonCompletionStatusUpdateCommand;
import com.everrefine.elms.application.dto.UserLessonDetailDto;
import com.everrefine.elms.application.dto.UserLessonGroupDto;
import java.util.List;

/** ユーザーレッスンアプリケーションサービスのインターフェース。 */
public interface UserLessonApplicationService {

  /**
   * ユーザーレッスン詳細を取得する。
   *
   * @param userId ユーザーID
   * @param courseId コースID
   * @param lessonGroupId レッスングループID
   * @param lessonId レッスンID
   * @return ユーザーレッスン詳細DTO
   */
  UserLessonDetailDto findUserLessonDetail(
      Integer userId, Integer courseId, Integer lessonGroupId, Integer lessonId);

  /**
   * ユーザーレッスンの完了状態を更新する。
   *
   * @param courseId コースID
   * @param lessonGroupId レッスングループID
   * @param userLessonCompletionStatusUpdateCommand ユーザーレッスン完了状態更新Command
   */
  void updateUserLesson(
      Integer courseId,
      Integer lessonGroupId,
      UserLessonCompletionStatusUpdateCommand userLessonCompletionStatusUpdateCommand);

  /**
   * ユーザーIDとコースIDに紐づくレッスン一覧をグループごとに取得する。
   *
   * @param userId ユーザーID
   * @param courseId コースID
   * @return ユーザーレッスングループDTOリスト
   */
  List<UserLessonGroupDto> findUserLessons(Integer userId, Integer courseId);
}
