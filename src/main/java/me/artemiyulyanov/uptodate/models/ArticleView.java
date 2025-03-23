package me.artemiyulyanov.uptodate.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Table(name = "articles_views")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    @JsonIgnore
    private Article article;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private String ipAddress;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime viewedAt;

    public Long getArticleId() {
        return article.getId();
    }

    public Long getUserId() {
        if (user == null) return null;
        return user.getId();
    }
}