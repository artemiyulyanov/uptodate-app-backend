package me.artemiyulyanov.uptodate.controllers.api.account.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import me.artemiyulyanov.uptodate.models.ArticleLike;
import me.artemiyulyanov.uptodate.models.ArticleView;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.web.ServerResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Component
public class StatisticsResponse extends ServerResponse<Void> {
    private List<ArticleView> lastViews;
    private List<ArticleLike> lastLikes;

    @JsonIgnore
    @Override
    public Void getResponse() {
        return super.getResponse();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
