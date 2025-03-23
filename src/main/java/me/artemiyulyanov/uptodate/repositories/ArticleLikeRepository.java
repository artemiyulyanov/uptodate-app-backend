package me.artemiyulyanov.uptodate.repositories;

import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.models.ArticleLike;
import me.artemiyulyanov.uptodate.models.ArticleView;
import me.artemiyulyanov.uptodate.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    @Query("SELECT e FROM ArticleLike e WHERE e.article.author = :author AND e.likedAt >= :after")
    List<ArticleLike> findLastLikesOfAuthor(@Param("author") User author, @Param("after") LocalDateTime after);

    Optional<ArticleLike> findByArticleAndUser(Article article, User user);

    boolean existsByArticleAndUser(Article article, User user);
}