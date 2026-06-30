package com.everrefine.elms.application.service;

import com.everrefine.elms.application.dto.UserCourseDto;
import java.util.List;

/** ユーザーコースアプリケーションサービスのインターフェース。 */
public interface UserCourseApplicationService {

  /**
   * ユーザーIDに紐づくコース一覧を取得する。
   *
   * @param userId ユーザーID
   * @return ユーザーコースDTOリスト
   */
  List<UserCourseDto> findUserCourses(Integer userId);
}
