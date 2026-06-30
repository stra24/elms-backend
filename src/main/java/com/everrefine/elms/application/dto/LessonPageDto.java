package com.everrefine.elms.application.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** レッスンページDTOに関するクラス。 */
@AllArgsConstructor
@Getter
public class LessonPageDto {

  /** レッスンDTOリスト */
  private final List<LessonDto> lessonDtos;

  /** ページ番号 */
  private final int pageNum;

  /** 1ページ当たりの件数 */
  private final int pageSize;

  /** 総データ件数 */
  private final int totalSize;

  /**
   * LessonPageDtoを生成する。
   *
   * @param lessonDtos レッスンDTOリスト
   * @param pageNum ページ番号
   * @param pageSize 1ページ当たりの件数
   * @param totalSize 総データ件数
   * @return レッスンページDTO
   */
  public static LessonPageDto from(
      List<LessonDto> lessonDtos, int pageNum, int pageSize, int totalSize) {
    return new LessonPageDto(lessonDtos, pageNum, pageSize, totalSize);
  }
}
