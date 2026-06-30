package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.news.News;
import com.everrefine.elms.domain.model.news.NewsSearchCondition;
import com.everrefine.elms.domain.repository.NewsRepository;
import com.everrefine.elms.infrastructure.dao.NewsDao;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

/** {@link NewsRepository} の実装クラス。 */
@Repository
@AllArgsConstructor
public class NewsRepositoryImpl implements NewsRepository {

  private final NewsDao newsDao;

  @Override
  public List<News> findNewsByIds(List<Integer> newsIds) {
    if (newsIds.isEmpty()) {
      return Collections.emptyList();
    }
    return newsDao.findByIdIn(newsIds);
  }

  @Override
  public int countNews(NewsSearchCondition newsSearchCondition) {
    return newsDao.countNewsBySearchConditions(
        newsSearchCondition.getTitle(),
        newsSearchCondition.getCreatedDateFrom() == null
            ? null
            : newsSearchCondition.getCreatedDateFrom(),
        newsSearchCondition.getCreatedDateTo() == null
            ? null
            : newsSearchCondition.getCreatedDateTo());
  }

  @Override
  public void createNews(News news) {
    newsDao.save(news);
  }

  @Override
  public void deleteNewsById(Integer id) {
    newsDao.deleteById(id);
  }

  @Override
  public void updateNews(News news) {
    newsDao.save(news);
  }

  @Override
  public List<Integer> findNewsIdsBySearchConditions(NewsSearchCondition newsSearchCondition) {
    return newsDao.findNewsBySearchConditions(
        newsSearchCondition.getTitle(),
        newsSearchCondition.getCreatedDateFrom() == null
            ? null
            : newsSearchCondition.getCreatedDateFrom(),
        newsSearchCondition.getCreatedDateTo() == null
            ? null
            : newsSearchCondition.getCreatedDateTo(),
        newsSearchCondition.getPagerForRequest().getPageSize(),
        newsSearchCondition.getPagerForRequest().getOffset());
  }

  @Override
  public Optional<News> findNewsById(Integer id) {
    return newsDao.findById(id);
  }
}
