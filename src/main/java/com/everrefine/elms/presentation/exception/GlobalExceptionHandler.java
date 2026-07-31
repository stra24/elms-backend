package com.everrefine.elms.presentation.exception;

import com.everrefine.elms.application.exception.BadRequestException;
import com.everrefine.elms.application.exception.ResourceNotFoundException;
import com.everrefine.elms.domain.exception.InvalidValueException;
import com.everrefine.elms.presentation.response.ErrorResponse;
import java.util.stream.Collectors;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/** グローバル例外ハンドラー。 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  // ① リクエストのバリデーション失敗 → 400
  /**
   * リクエストボディのバリデーション失敗を処理する。
   *
   * @param e バリデーション例外
   * @return エラーレスポンス
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleValidation(MethodArgumentNotValidException e) {
    String message =
        e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
    return new ErrorResponse("VALIDATION_ERROR", message);
  }

  // ② パスパラメータ・クエリパラメータのバリデーション失敗 → 400
  /**
   * パスパラメータ・クエリパラメータのバリデーション失敗を処理する。
   *
   * @param e バリデーション例外
   * @return エラーレスポンス
   */
  @ExceptionHandler(HandlerMethodValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleMethodValidation(HandlerMethodValidationException e) {
    String message =
        e.getParameterValidationResults().stream()
            .flatMap(result -> result.getResolvableErrors().stream())
            .map(MessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining(", "));
    return new ErrorResponse("VALIDATION_ERROR", message);
  }

  // ③ 必須リクエストパラメータ不足 → 400
  /**
   * 必須リクエストパラメータ不足を処理する。
   *
   * @param e パラメータ不足例外
   * @return エラーレスポンス
   */
  @ExceptionHandler({
    MissingServletRequestParameterException.class,
    MissingServletRequestPartException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleMissingRequestParameter(Exception e) {
    return new ErrorResponse("VALIDATION_ERROR", e.getMessage());
  }

  // ④ クライアント入力不正 → 400
  /**
   * クライアント入力不正を処理する。
   *
   * @param e 入力不正例外
   * @return エラーレスポンス
   */
  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleBadRequest(BadRequestException e) {
    return new ErrorResponse("BAD_REQUEST", e.getMessage());
  }

  // ⑤ リソース未検出 → 404
  // catch-all より先に @ExceptionHandler が解決されるため、@ResponseStatus だけでは 404 にならない。
  // ここで明示的にハンドリングする。
  /**
   * リソース未検出例外を処理する。
   *
   * @param e リソース未検出例外
   * @return エラーレスポンス
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleNotFound(ResourceNotFoundException e) {
    return new ErrorResponse("RESOURCE_NOT_FOUND", e.getMessage());
  }

  // ⑥ DBデータ不整合（値オブジェクト生成失敗） → 500
  /**
   * 不正値例外を処理する。
   *
   * @param e 不正値例外
   * @return エラーレスポンス
   */
  @ExceptionHandler(InvalidValueException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleInvalidValue(InvalidValueException e) {
    return new ErrorResponse("INVALID_VALUE", e.getMessage());
  }

  // ⑦ 想定外のすべて → 500
  /**
   * 想定外の例外を処理する。
   *
   * @param e 例外
   * @return エラーレスポンス
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleGeneral(Exception e) {
    return new ErrorResponse("INTERNAL_ERROR", e.getMessage());
  }
}
