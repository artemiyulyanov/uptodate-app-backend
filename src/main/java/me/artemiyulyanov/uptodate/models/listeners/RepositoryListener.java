package me.artemiyulyanov.uptodate.models.listeners;

import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.models.Comment;
import me.artemiyulyanov.uptodate.models.UserStatistics;
import me.artemiyulyanov.uptodate.repositories.UserRepository;
import me.artemiyulyanov.uptodate.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepositoryListener {
    @Autowired
    public RepositoryListener(CommentService commentService, ArticleService articleService, ArticleViewService articleViewService, ArticleLikeService articleLikeService, UserService userService) {
        Comment.setCommentService(commentService);
        Comment.setUserService(userService);

        UserStatistics.setArticleLikeService(articleLikeService);
        UserStatistics.setArticleViewService(articleViewService);

        Article.setUserService(userService);
    }
}