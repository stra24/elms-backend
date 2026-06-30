package com.everrefine.elms.application.service;

import com.everrefine.elms.application.command.LessonGroupCreateCommand;
import com.everrefine.elms.application.command.LessonGroupUpdateCommand;
import com.everrefine.elms.application.dto.LessonDto;
import com.everrefine.elms.application.dto.LessonGroupDto;
import com.everrefine.elms.application.exception.ResourceNotFoundException;
import com.everrefine.elms.domain.model.lesson.LessonGroup;
import com.everrefine.elms.domain.repository.LessonGroupRepository;
import com.everrefine.elms.domain.repository.LessonRepository;
import com.everrefine.elms.domain.service.LessonGroupDomainService;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** レッスングループアプリケーションサービスの実装に関するクラス。 */
@Service
@AllArgsConstructor
public class LessonGroupApplicationServiceImpl implements LessonGroupApplicationService {

  private final LessonGroupRepository lessonGroupRepository;
  private final LessonRepository lessonRepository;
  private final LessonGroupDomainService lessonGroupDomainService;

  @Override
  @Transactional
  public LessonGroupDto createLessonGroup(LessonGroupCreateCommand lessonGroupCreateCommand) {
    BigDecimal lessonGroupOrder =
        lessonGroupDomainService.issueLessonGroupOrder(lessonGroupCreateCommand.getCourseId());
    LessonGroup createdLessonGroup =
        lessonGroupRepository.createLessonGroup(
            lessonGroupCreateCommand.toLessonGroup(lessonGroupOrder));
    return LessonGroupDto.from(createdLessonGroup);
  }

  @Override
  @Transactional
  public LessonGroupDto updateLessonGroup(LessonGroupUpdateCommand lessonGroupUpdateCommand) {
    LessonGroup lessonGroup =
        lessonGroupRepository
            .findLessonGroupById(lessonGroupUpdateCommand.getId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        LessonGroup.class, String.valueOf(lessonGroupUpdateCommand.getId())));
    LessonGroup persistedLessonGroup =
        lessonGroupRepository.updateLessonGroup(
            lessonGroupUpdateCommand.toLessonGroup(lessonGroup));

    List<LessonDto> lessonDtos =
        lessonRepository.findLessonsByLessonGroupId(persistedLessonGroup.getId()).stream()
            .map(LessonDto::from)
            .toList();

    return LessonGroupDto.from(persistedLessonGroup, lessonDtos);
  }

  @Override
  @Transactional
  public void deleteLessonGroupById(Integer lessonGroupId) {
    // レッスングループが存在しなくてもエラーにはしない。
    lessonGroupRepository
        .findLessonGroupById(lessonGroupId)
        .ifPresent(LessonGroup -> lessonGroupRepository.deleteLessonGroupById(lessonGroupId));
  }
}
