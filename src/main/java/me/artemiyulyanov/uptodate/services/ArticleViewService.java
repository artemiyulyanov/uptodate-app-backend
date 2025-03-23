package me.artemiyulyanov.uptodate.services;

import jakarta.servlet.http.HttpServletRequest;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.models.ArticleView;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.repositories.ArticleViewRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ArticleViewService {
    @Autowired
    private ObjectFactory<HttpServletRequest> requestFactory;

    @Autowired
    private ArticleViewRepository articleViewRepository;

    @Transactional
    public void view(Article article, User user) {
        HttpServletRequest request = requestFactory.getObject();
        String ipAddress = request.getRemoteAddr();

        boolean alreadyViewed = article.getViews().stream()
                .anyMatch(view -> (user != null && view.getUser() == user && ChronoUnit.HOURS.between(LocalDateTime.now(), view.getViewedAt()) < 24) || (user == null && view.getIpAddress().equals(ipAddress)));

        if (alreadyViewed) return;

        ArticleView view = ArticleView.builder()
                .article(article)
                .user(user)
                .ipAddress(ipAddress)
                .viewedAt(LocalDateTime.now())
                .build();

        article.getViews().add(view);
        articleViewRepository.save(view);
    }

    @Transactional(readOnly = true)
    public List<ArticleView> findLastViewsOfAuthor(User user, LocalDateTime after) {
        return articleViewRepository.findLastViewsOfAuthor(user, after);
    }
}