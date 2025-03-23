package me.artemiyulyanov.uptodate.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.artemiyulyanov.uptodate.services.ArticleLikeService;
import me.artemiyulyanov.uptodate.services.ArticleViewService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UserStatistics {
    @Getter
    @Setter
    @Autowired
    private static ArticleViewService articleViewService;

    @Getter
    @Setter
    @Autowired
    private static ArticleLikeService articleLikeService;

    @JsonIgnore
    private User user;

    private List<ArticleView> lastViews;
    private List<ArticleLike> lastLikes;

    @Builder
    public UserStatistics(User user) {
        this.user = user;
        this.lastViews = articleViewService.findLastViewsOfAuthor(user, LocalDateTime.now().minusDays(1));
        this.lastLikes = articleLikeService.findLastLikesOfAuthor(user, LocalDateTime.now().minusDays(1));
    }

    public Long getUserId() {
        return user.getId();
    }
}