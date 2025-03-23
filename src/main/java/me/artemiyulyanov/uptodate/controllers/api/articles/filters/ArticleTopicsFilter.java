package me.artemiyulyanov.uptodate.controllers.api.articles.filters;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.repositories.specifications.ArticleSpecification;
import me.artemiyulyanov.uptodate.web.PageableObject;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@Component
public class ArticleTopicsFilter extends ArticleFilter<List<String>> {
    @Override
    public PageableObject<Article> applyFilter(PageableObject<Article> pageableObject) {
        pageableObject.addSpecification(ArticleSpecification.filterByTopics(this.getValue()));
        return pageableObject;
    }
}