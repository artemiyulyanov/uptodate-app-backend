package me.artemiyulyanov.uptodate.repositories.specifications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.models.Category;
import me.artemiyulyanov.uptodate.models.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ArticleSpecification {
    public static Specification<Article> filterByTopics(List<String> categories) {
        return (root, q, criteriaBuilder) -> {
            if (categories == null || categories.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            Join<Article, Category> categoriesJoin = root.join("categories");

            List<Predicate> predicates = categories.stream()
                    .map(category -> criteriaBuilder.or(
                            criteriaBuilder.equal(criteriaBuilder.function("JSON_UNQUOTE", String.class,
                                    criteriaBuilder.function("JSON_EXTRACT", String.class,
                                            categoriesJoin.get("name"), criteriaBuilder.literal("$.english"))), category),
                            criteriaBuilder.equal(criteriaBuilder.function("JSON_UNQUOTE", String.class,
                                    criteriaBuilder.function("JSON_EXTRACT", String.class,
                                            categoriesJoin.get("name"), criteriaBuilder.literal("$.russian"))), category)
                    ))
                    .toList();

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Article> filterByQuery(String query) {
        return (root, q, criteriaBuilder) -> {
            String pattern = "%" + query.toLowerCase() + "%";

            Join<Article, User> authorJoin = root.join("author");

            Predicate headingContaining = criteriaBuilder.like(criteriaBuilder.lower(root.get("heading")), pattern);
            Predicate descriptionContaining = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern);
            Predicate authorUsernameContaining = criteriaBuilder.like(criteriaBuilder.lower(authorJoin.get("username")), pattern);

            return criteriaBuilder.or(headingContaining, descriptionContaining, authorUsernameContaining);
        };
    }
}