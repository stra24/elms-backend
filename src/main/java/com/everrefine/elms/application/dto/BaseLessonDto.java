package com.everrefine.elms.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** レッスン情報の基底 DTO。 */
@AllArgsConstructor
@Getter
public abstract class BaseLessonDto {

  @Schema(description = "レッスンID", example = "1")
  private final Integer id;

  @Schema(description = "レッスングループID", example = "2")
  private final Integer lessonGroupId;

  @Schema(description = "コースID", example = "3")
  private final Integer courseId;

  @Schema(description = "レッスンの表示順", example = "1.0")
  private final BigDecimal lessonOrder;

  @Schema(description = "レッスンタイトル", example = "変数とデータ型")
  private final String title;

  @Schema(description = "レッスン本文（Markdown対応）", example = "## 変数とは\n変数はデータを格納する箱です。")
  private final String content;

  @Schema(description = "動画URL", example = "https://example.com/videos/lesson1.mp4")
  private final String videoUrl;

  @Schema(description = "登録日時", example = "2024-01-01T09:00:00")
  private final LocalDateTime createdAt;

  @Schema(description = "更新日時", example = "2024-06-01T10:30:00")
  private final LocalDateTime updatedAt;
}
