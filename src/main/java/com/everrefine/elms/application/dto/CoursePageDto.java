package com.everrefine.elms.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** コースページDTOに関するクラス。 */
@AllArgsConstructor
@Getter
public class CoursePageDto {

  @Schema(description = "コース一覧")
  private final List<CourseDto> courseDtos;

  @Schema(description = "現在のページ番号", example = "1")
  private final int pageNum;

  @Schema(description = "1ページ当たりの件数", example = "10")
  private final int pageSize;

  @Schema(description = "総データ件数", example = "5")
  private final int totalSize;

  /**
   * CoursePageDtoを生成する。
   *
   * @param courseDtos コースDTOリスト
   * @param pageNum ページ番号
   * @param pageSize 1ページ当たりの件数
   * @param totalSize 総データ件数
   * @return コースページDTO
   */
  public static CoursePageDto from(
      List<CourseDto> courseDtos, int pageNum, int pageSize, int totalSize) {
    return new CoursePageDto(courseDtos, pageNum, pageSize, totalSize);
  }
}
