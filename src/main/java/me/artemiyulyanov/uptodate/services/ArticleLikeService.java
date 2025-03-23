package me.artemiyulyanov.uptodate.services;

import jakarta.persistence.Transient;
import me.artemiyulyanov.uptodate.models.*;
import me.artemiyulyanov.uptodate.repositories.ArticleLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArticleLikeService {
    @Autowired
    private ArticleLikeRepository articleLikeRepository;

    @Autowired
    private ArticleService articleService;

    @Transactional
    public Article like(Article article, User user) {
        List<ArticleLike> likes = article.getLikes();
        boolean alreadyLiked = likes.stream().anyMatch(like -> like.getUser().getId().equals(user.getId()));

        if (!alreadyLiked) {
            ArticleLike articleLike = ArticleLike
                    .builder()
                    .article(article)
                    .user(user)
                    .likedAt(LocalDateTime.now())
                    .build();

            likes.add(articleLike);
        } else {
            likes.removeIf(like -> like.getUser().getId().equals(user.getId()));
        }

        article.setLikes(likes);
        return articleService.save(article);
    }

    @Transactional(readOnly = true)
    public List<ArticleLike> findLastLikesOfAuthor(User user, LocalDateTime after) {
        return articleLikeRepository.findLastLikesOfAuthor(user, after);
    }
}