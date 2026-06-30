package com.everrefine.elms.application.service;

import com.everrefine.elms.application.command.LessonCreateCommand;
import com.everrefine.elms.application.command.LessonOrderUpdateCommand;
import com.everrefine.elms.application.command.LessonSearchCommand;
import com.everrefine.elms.application.command.LessonUpdateCommand;
import com.everrefine.elms.application.dto.*;
import com.everrefine.elms.application.exception.ResourceNotFoundException;
import com.everrefine.elms.domain.model.lesson.Lesson;
import com.everrefine.elms.domain.model.lesson.LessonGroupWithLesson;
import com.everrefine.elms.domain.model.lesson.LessonWithCourseAndLessonGroup;
import com.everrefine.elms.domain.repository.LessonRepository;
import com.everrefine.elms.domain.service.LessonDomainService;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** レッスンアプリケーションサービスの実装に関するクラス。 */
@Service
@AllArgsConstructor
public class LessonApplicationServiceImpl implements LessonApplicationService {

  private final LessonRepository lessonRepository;
  private final LessonDomainService lessonDomainService;

  /**
   * CSV出力用に値をエスケープする。
   *
   * <p>値にカンマ、ダブルクォーテーション、改行があった場合のエスケープ処理。
   *
   * @param value エスケープ対象の文字列
   * @return エスケープ後の文字列
   */
  private String escape(String value) {
    if (value == null) {
      return "";
    }
    if (value.contains(",")
        || value.contains("\"")
        || value.contains("\n")
        || value.contains("\r")) {
      return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    return value;
  }

  @Override
  @Transactional(readOnly = true)
  public LessonDto findLessonById(Integer courseId, Integer lessonGroupId, Integer lessonId) {
    Lesson lesson = findLessonBelongingToCourseAndGroupOrThrow(lessonId, courseId, lessonGroupId);
    return LessonDto.from(lesson);
  }

  @Override
  @Transactional(readOnly = true)
  public LessonPageDto findLessons(LessonSearchCommand lessonSearchCommand) {
    List<Lesson> lessons = lessonRepository.findLessons(lessonSearchCommand.toCriteria());
    int totalSize = lessonRepository.countLessons(lessonSearchCommand.toCriteria());

    List<LessonDto> lessonDtos = lessons.stream().map(LessonDto::from).toList();

    return LessonPageDto.from(
        lessonDtos, lessonSearchCommand.getPageNum(), lessonSearchCommand.getPageSize(), totalSize);
  }

  /**
   * コースとレッスングループに属するレッスンを取得する。存在しない場合は例外をスローする。
   *
   * @param lessonId レッスンID
   * @param courseId コースID
   * @param lessonGroupId レッスングループID
   * @return レッスンエンティティ
   */
  private Lesson findLessonBelongingToCourseAndGroupOrThrow(
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
  public CourseLessonsDto findLessonsGroupedByLessonGroup(Integer courseId) {
    List<LessonGroupWithLesson> lessons =
        lessonRepository.findLessonsGroupedByLessonGroup(courseId);
    Map<Integer, List<LessonGroupWithLesson>> lessonGroupIdAndLessonsMap =
        lessons.stream().collect(Collectors.groupingBy(LessonGroupWithLesson::getLessonGroupId));
    List<LessonGroupDto> lessonGroupDtos =
        lessonGroupIdAndLessonsMap.values().stream().map(LessonGroupDto::from).toList();
    return new CourseLessonsDto(courseId, lessonGroupDtos);
  }

  @Override
  @Transactional
  public LessonDto createLesson(LessonCreateCommand lessonCreateCommand) {
    BigDecimal lessonOrder =
        lessonDomainService.issueLessonOrder(lessonCreateCommand.getLessonGroupId());
    Lesson createdLesson = lessonRepository.createLesson(lessonCreateCommand.toLesson(lessonOrder));
    return LessonDto.from(createdLesson);
  }

  @Override
  @Transactional
  public LessonDto updateLesson(LessonUpdateCommand lessonUpdateCommand) {
    Lesson currentLesson = findLessonOrThrow(lessonUpdateCommand.getId());
    Lesson updatedLesson =
        lessonRepository.updateLesson(lessonUpdateCommand.toLesson(currentLesson));
    return LessonDto.from(updatedLesson);
  }

  @Override
  @Transactional
  public void deleteLessonById(Integer lessonId) {
    lessonRepository
        .findById(lessonId)
        .ifPresent(lesson -> lessonRepository.deleteLessonById(lessonId));
  }

  /**
   * レッスンIDに対応するレッスン順序を返す。IDがnullの場合はnullを返す。
   *
   * @param lessonId レッスンID（nullの場合はnullを返す）
   * @param lessonIdAndLessonMap レッスンIDとレッスンのMap
   * @return レッスン順序（lessonIdがnullの場合はnull）
   */
  private BigDecimal resolveLessonOrderOrNull(
      Integer lessonId, Map<Integer, Lesson> lessonIdAndLessonMap) {
    if (lessonId == null) {
      return null;
    }

    Lesson lesson = lessonIdAndLessonMap.get(lessonId);
    if (lesson == null) {
      throw new ResourceNotFoundException(Lesson.class, String.valueOf(lessonId));
    }

    return lesson.getLessonOrder().getValue();
  }

  @Override
  @Transactional
  public LessonDto updateLessonOrder(LessonOrderUpdateCommand lessonOrderUpdateCommand) {
    Integer targetLessonId = lessonOrderUpdateCommand.getLessonId();
    Integer precedingLessonId = lessonOrderUpdateCommand.getPrecedingLessonId();
    Integer followingLessonId = lessonOrderUpdateCommand.getFollowingLessonId();

    List<Integer> lessonIds = new ArrayList<>();
    lessonIds.add(targetLessonId);
    if (precedingLessonId != null && !lessonIds.contains(precedingLessonId)) {
      lessonIds.add(precedingLessonId);
    }
    if (followingLessonId != null && !lessonIds.contains(followingLessonId)) {
      lessonIds.add(followingLessonId);
    }

    Map<Integer, Lesson> lessonIdAndLessonMap =
        lessonRepository.findByIdIn(lessonIds).stream()
            .collect(Collectors.toMap(Lesson::getId, Function.identity()));

    Lesson targetLesson = lessonIdAndLessonMap.get(targetLessonId);
    if (targetLesson == null) {
      throw new ResourceNotFoundException(Lesson.class, String.valueOf(targetLessonId));
    }

    BigDecimal precedingOrder = resolveLessonOrderOrNull(precedingLessonId, lessonIdAndLessonMap);

    BigDecimal followingOrder = resolveLessonOrderOrNull(followingLessonId, lessonIdAndLessonMap);

    BigDecimal newOrder = lessonDomainService.calculateNewOrder(precedingOrder, followingOrder);

    Lesson updatedLesson = targetLesson.updateOrder(newOrder);
    Lesson savedLesson = lessonRepository.updateLesson(updatedLesson);

    return LessonDto.from(savedLesson);
  }

  @Transactional(readOnly = true)
  @Override
  public Resource exportAllLessonsCsv() {

    String[] header = {
      "コースID", "コース名", "レッスングループID", "レッスングループ名", "レッスンID", "レッスンタイトル", "レッスンの動画URL"
    };

    List<LessonWithCourseAndLessonGroup> allLessons = lessonRepository.findAllLessons();

    if (allLessons.isEmpty()) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try (PrintWriter file =
          new PrintWriter(
              new BufferedWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8)))) {
        file.print('\uFEFF');
        file.println(String.join(",", header));
      } catch (Exception e) {
        throw new RuntimeException("CSVファイルの作成に失敗しました", e);
      }
      return new ByteArrayResource(baos.toByteArray());
    }

    List<LessonWithCourseAndLessonGroupDto> dtos =
        allLessons.stream().map(LessonWithCourseAndLessonGroupDto::from).toList();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try (
    // 書き込むファイルを作成
    PrintWriter file =
        new PrintWriter(
            new BufferedWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))); ) {
      // Excelで開いたときの文字化けを防ぐ
      file.print('\uFEFF');

      // ヘッダーをセットする
      file.println(String.join(",", header));

      // 中身をセットする ( 1レッスンごとに改行 )
      for (LessonWithCourseAndLessonGroupDto dto : dtos) {
        String[] lessons = {
          escape(String.valueOf(dto.getCourseId())),
          escape(dto.getCourseTitle()),
          escape(String.valueOf(dto.getLessonGroupId())),
          escape(dto.getLessonGroupTitle()),
          escape(String.valueOf(dto.getLessonId())),
          escape(dto.getLessonTitle()),
          escape(dto.getVideoUrl())
        };
        file.println(String.join(",", lessons));
      }
    } catch (Exception e) {
      throw new RuntimeException("CSVファイルの作成に失敗しました", e);
    }
    return new ByteArrayResource(baos.toByteArray());
  }
}
