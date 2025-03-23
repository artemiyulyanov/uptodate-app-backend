package me.artemiyulyanov.uptodate.repositories;

import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.repositories.specifications.ArticleSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {
    List<Article> findByAuthor(User author);
    Optional<Article> findBySlug(String slug);

//    @Query("SELECT a FROM Article a WHERE a.author = :user AND a.createdAt >= :after")
//    List<Article> findArticlesByAuthorAfterDate(@Param("user") User user, @Param("after") LocalDateTime after);
}