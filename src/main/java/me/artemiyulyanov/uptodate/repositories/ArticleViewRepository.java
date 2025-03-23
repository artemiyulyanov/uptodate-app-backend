package me.artemiyulyanov.uptodate.repositories;

import me.artemiyulyanov.uptodate.models.ArticleView;
import me.artemiyulyanov.uptodate.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArticleViewRepository extends JpaRepository<ArticleView, Long> {
    @Query("SELECT e FROM ArticleView e WHERE e.article.author = :user AND e.viewedAt >= :after")
    List<ArticleView> findLastViewsOfAuthor(@Param("user") User user, @Param("after") LocalDateTime after);
}