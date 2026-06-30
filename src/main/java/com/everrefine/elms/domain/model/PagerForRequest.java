package com.everrefine.elms.domain.model;

import com.everrefine.elms.domain.exception.InvalidValueException;
import lombok.Value;

/** データ取得のリクエスト時に使うページャーの値オブジェクト。 */
@Value
public class PagerForRequest {

  /** ページ番号 */
  int pageNum;

  /** 1ページ当たりの件数 */
  int pageSize;

  /**
   * ページャーを作成する。
   *
   * @param pageNum ページ番号（1以上）
   * @param pageSize 1ページ当たりの件数（1以上）
   */
  public PagerForRequest(int pageNum, int pageSize) {
    if (pageNum < 1) {
      throw new InvalidValueException("ページ番号は1以上を指定してください");
    }
    if (pageSize < 1) {
      throw new InvalidValueException("ページサイズは1以上を指定してください");
    }
    this.pageNum = pageNum;
    this.pageSize = pageSize;
  }

  /**
   * DBクエリ用のオフセット値を返す。
   *
   * @return オフセット値
   */
  public int getOffset() {
    return (pageNum - 1) * pageSize;
  }
}
