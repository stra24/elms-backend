package com.everrefine.elms.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** CSV取込レスポンス用DTO。 取込した件数を返却する。 */
@Getter
@AllArgsConstructor
public class UserImportResponseDto {

  private int importedCount;
}
