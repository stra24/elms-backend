package com.everrefine.elms.domain.service;

import com.everrefine.elms.domain.model.Order;
import com.everrefine.elms.domain.repository.LessonGroupRepository;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/** {@link LessonGroupDomainService} の実装クラス。 */
@Component
@AllArgsConstructor
public class LessonGroupDomainServiceImpl implements LessonGroupDomainService {

  private final LessonGroupRepository lessonGroupRepository;

  @Override
  public BigDecimal issueLessonGroupOrder(Integer courseId) {
    return lessonGroupRepository
        .findMaxLessonGroupOrderByCourseId(courseId)
        .map(maxOrder -> maxOrder.add(Order.INTERVAL_ORDER))
        .orElse(BigDecimal.ONE);
  }
}
