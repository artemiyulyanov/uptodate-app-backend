package me.artemiyulyanov.uptodate.controllers.api.articles.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.repositories.specifications.ArticleSpecification;
import me.artemiyulyanov.uptodate.web.PageableObject;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@Component
public class ArticleQueryFilter extends ArticleFilter<String> {
    @Override
    public PageableObject<Article> applyFilter(PageableObject<Article> pageableObject) {
        pageableObject.addSpecification(ArticleSpecification.filterByQuery(this.getValue()));
        return pageableObject;
    }
}