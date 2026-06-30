package com.everrefine.elms.domain.model.lesson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.everrefine.elms.domain.exception.InvalidValueException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class LessonGroupTest {

  @Test
  void 正常系_createで新しいレッスングループが作成されること() {
    // Arrange & Act
    LessonGroup lessonGroup = LessonGroup.create(100, new BigDecimal("1"), "テストグループ");

    // Assert
    assertEquals(null, lessonGroup.getId());
    assertEquals(100, lessonGroup.getCourseId());
    assertEquals(new BigDecimal("1"), lessonGroup.getLessonGroupOrder().getValue());
    assertEquals("テストグループ", lessonGroup.getTitle().getValue());
    assertNotNull(lessonGroup.getCreatedAt()); // createメソッドで設定される
    assertNotNull(lessonGroup.getUpdatedAt()); // createメソッドで設定される
  }

  @Test
  void 正常系_updateで新しいインスタンスが作成され不変性が保たれること() {
    // Arrange
    LocalDateTime originalCreatedAt = LocalDateTime.of(2025, 12, 10, 10, 0);
    LocalDateTime originalUpdatedAt = LocalDateTime.of(2025, 12, 10, 10, 0);

    LessonGroup original =
        new LessonGroup(
            1,
            100,
            new com.everrefine.elms.domain.model.Order(new BigDecimal("1")),
            new Title("元のタイトル"),
            originalCreatedAt,
            originalUpdatedAt);

    // Act
    LocalDateTime beforeUpdate = LocalDateTime.now();
    LessonGroup updated = original.update("新しいタイトル");
    LocalDateTime afterUpdate = LocalDateTime.now();

    // Assert - 新しいインスタンスが作成されていること
    assertNotEquals(original, updated);

    // Assert - 不変なフィールドが保持されていること
    assertEquals(original.getId(), updated.getId());
    assertEquals(original.getCourseId(), updated.getCourseId());
    assertEquals(original.getLessonGroupOrder(), updated.getLessonGroupOrder());
    assertEquals(original.getCreatedAt(), updated.getCreatedAt());

    // Assert - タイトルが更新されていること
    assertEquals("新しいタイトル", updated.getTitle().getValue());
    assertEquals("元のタイトル", original.getTitle().getValue());

    // Assert - updatedAtが更新されていること
    assertNotEquals(original.getUpdatedAt(), updated.getUpdatedAt());
    assertTrue(
        updated.getUpdatedAt().isAfter(beforeUpdate)
            || updated.getUpdatedAt().isEqual(beforeUpdate));
    assertTrue(
        updated.getUpdatedAt().isBefore(afterUpdate)
            || updated.getUpdatedAt().isEqual(afterUpdate));

    // Assert - 元のインスタンスは変更されていないこと
    assertEquals("元のタイトル", original.getTitle().getValue());
    assertEquals(originalUpdatedAt, original.getUpdatedAt());
  }

  @Test
  void 正常系_updateで同じタイトルを設定しても新しいインスタンスが作成されること() {
    // Arrange
    LocalDateTime originalCreatedAt = LocalDateTime.of(2025, 12, 10, 10, 0);
    LocalDateTime originalUpdatedAt = LocalDateTime.of(2025, 12, 10, 10, 0);

    LessonGroup original =
        new LessonGroup(
            1,
            100,
            new com.everrefine.elms.domain.model.Order(new BigDecimal("1")),
            new Title("同じタイトル"),
            originalCreatedAt,
            originalUpdatedAt);

    // Act
    LessonGroup updated = original.update("同じタイトル");

    // Assert - 新しいインスタンスが作成されていること
    assertNotEquals(original, updated);
    // Assert - updatedAtは更新されること（LocalDateTime.now()が呼ばれるため）
    assertNotEquals(original.getUpdatedAt(), updated.getUpdatedAt());
  }

  @Test
  void 正常系_updateで空文字を設定できること() {
    // Arrange
    LessonGroup original =
        new LessonGroup(
            1,
            100,
            new com.everrefine.elms.domain.model.Order(new BigDecimal("1")),
            new Title("元のタイトル"),
            LocalDateTime.now(),
            LocalDateTime.now());

    // Act
    LessonGroup updated = original.update("");

    // Assert
    assertEquals("", updated.getTitle().getValue());
  }

  @Test
  void 異常系_updateでnullを設定すると例外がスローされること() {
    // Arrange
    LessonGroup original =
        new LessonGroup(
            1,
            100,
            new com.everrefine.elms.domain.model.Order(new BigDecimal("1")),
            new Title("元のタイトル"),
            LocalDateTime.now(),
            LocalDateTime.now());

    // Act & Assert
    assertThrows(InvalidValueException.class, () -> original.update(null));
  }
}
