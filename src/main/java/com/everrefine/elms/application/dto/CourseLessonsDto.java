package com.everrefine.elms.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** コースとレッスン一覧DTOに関するクラス。 */
public record CourseLessonsDto(
    @Schema(description = "コースID", example = "1") Integer courseId,
    @Schema(description = "レッスングループ一覧（各グループにレッスン一覧を含む）") List<LessonGroupDto> lessonGroups) {}
