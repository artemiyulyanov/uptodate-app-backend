package me.artemiyulyanov.uptodate.controllers.api.articles.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.web.PageableObject;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@Component
public class ArticleSortByFilter extends ArticleFilter<ArticleSortByFilter.ArticleSortByFilterValue> {
    @Override
    public PageableObject<Article> applyFilter(PageableObject<Article> pageableObject) {
        if (this.getValue() == ArticleSortByFilterValue.ASCENDING) {
            pageableObject.setSort(Sort.by(Sort.Order.asc("createdAt")));
        } else if (this.getValue() == ArticleSortByFilterValue.DESCENDING) {
            pageableObject.setSort(Sort.by(Sort.Order.desc("createdAt")));
        } else if (this.getValue() == ArticleSortByFilterValue.ALPHABETICALLY) {
            pageableObject.setSort(Sort.by(Sort.Order.asc("heading")));
        }

        return pageableObject;
    }

    enum ArticleSortByFilterValue {
        ASCENDING, DESCENDING, ALPHABETICALLY;
    }
}