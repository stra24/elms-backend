package com.everrefine.elms.application.dto;

/** ユーザーレッスンDTOに関するクラス。 */
public record UserLessonDto(LessonDto lesson, boolean isLessonCompleted) {}
