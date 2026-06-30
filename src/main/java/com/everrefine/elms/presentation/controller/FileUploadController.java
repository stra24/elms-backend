package com.everrefine.elms.presentation.controller;

import com.everrefine.elms.application.service.FileStorageApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** ファイルアップロードに関するコントローラー。 */
@Tag(name = "ファイルアップロード")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

  private final FileStorageApplicationService fileStorageApplicationService;

  /**
   * 画像ファイルをアップロードし、保存先パスを返す。
   *
   * @param file アップロード対象の画像ファイル
   * @return 保存先パス
   */
  @Operation(summary = "ファイルアップロード", description = "画像ファイルをアップロードし、保存先パスを返します")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "アップロード成功（レスポンスボディにファイルパスを返す）"),
    @ApiResponse(responseCode = "400", description = "ファイルアップロード失敗"),
    @ApiResponse(responseCode = "401", description = "認証されていません"),
    @ApiResponse(responseCode = "403", description = "管理者権限が必要です")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("/upload")
  public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
    try {
      String filePath = fileStorageApplicationService.saveImage(file);
      return ResponseEntity.ok(filePath); // 保存先のパスを返す
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("ファイルアップロード失敗: " + e.getMessage());
    }
  }
}
