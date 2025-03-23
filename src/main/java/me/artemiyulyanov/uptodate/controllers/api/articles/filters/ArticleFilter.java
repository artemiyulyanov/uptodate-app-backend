package me.artemiyulyanov.uptodate.controllers.api.articles.filters;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.web.PageableObject;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Component
public abstract class ArticleFilter<T> {
    private ArticleFilterType type;
    private T value;

    enum ArticleFilterType {
        QUERY, CATEGORIES, SORT_BY;
    }

    public abstract PageableObject<Article> applyFilter(PageableObject<Article> pageableObject);

    public static ArticleFilter of(String type, Object value) {
        if (ArticleFilterType.valueOf(type) == ArticleFilterType.CATEGORIES) {
            return ArticleTopicsFilter.builder()
                    .type(ArticleFilterType.valueOf(type))
                    .value((List<String>) value)
                    .build();
        }

        if (ArticleFilterType.valueOf(type) == ArticleFilterType.SORT_BY) {
            return ArticleSortByFilter.builder()
                    .type(ArticleFilterType.valueOf(type))
                    .value(
                            ArticleSortByFilter.ArticleSortByFilterValue.valueOf(
                                    String.valueOf(value).toUpperCase()
                            )
                    )
                    .build();
        }

        if (ArticleFilterType.valueOf(type) == ArticleFilterType.QUERY) {
            return ArticleQueryFilter.builder()
                    .type(ArticleFilterType.valueOf(type))
                    .value((String) value)
                    .build();
        }

        return null;
    }

    public static PageableObject<Article> applyFilters(PageableObject<Article> oldPageableObject, HashMap<String, Object> filters) {
        return filters.entrySet()
                .stream()
                .map(entry -> ArticleFilter.of(entry.getKey().toUpperCase(), entry.getValue()))
                .reduce(oldPageableObject,
                        (pageableObject, articleFilter) -> articleFilter.applyFilter(pageableObject),
                        (p1, p2) -> p1);
    }
}