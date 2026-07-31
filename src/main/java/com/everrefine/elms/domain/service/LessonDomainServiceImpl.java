package com.everrefine.elms.domain.service;

import com.everrefine.elms.domain.exception.InvalidValueException;
import com.everrefine.elms.domain.model.Order;
import com.everrefine.elms.domain.repository.LessonRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** {@link LessonDomainService} の実装。 */
@Component
@RequiredArgsConstructor
public class LessonDomainServiceImpl implements LessonDomainService {

  private final LessonRepository lessonRepository;

  @Override
  public BigDecimal issueLessonOrder(UUID lessonGroupId) {
    return lessonRepository
        .findMaxLessonOrderByLessonGroupId(lessonGroupId)
        .map(maxOrder -> maxOrder.add(Order.INTERVAL_ORDER))
        .orElse(Order.FIRST_ORDER);
  }

  @Override
  public BigDecimal calculateNewOrder(BigDecimal precedingOrder, BigDecimal followingOrder) {
    if (precedingOrder == null && followingOrder == null) {
      throw new InvalidValueException("前後の並び順が両方nullにはできません");
    }

    if (precedingOrder == null) {
      return followingOrder.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
    }

    if (followingOrder == null) {
      return precedingOrder.add(Order.INTERVAL_ORDER);
    }

    return precedingOrder.add(followingOrder).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
  }
}
