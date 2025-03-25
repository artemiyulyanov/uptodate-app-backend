package me.artemiyulyanov.uptodate.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import me.artemiyulyanov.uptodate.models.converters.DatabaseContentConverter;
import me.artemiyulyanov.uptodate.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.*;
import java.util.*;

@Entity
@Table(name = "articles")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    @Setter
    private static UserService userService;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String heading, description, cover;

    @Column(unique = true)
    private String slug;

    @Builder.Default
    @Convert(converter = DatabaseContentConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<ContentBlock> content = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleView> views = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleLike> likes = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "articles_categories",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Transient
    @Builder.Default
    private List<PermissionScope> permissionScope = new ArrayList<>();

    @PostLoad
    public void init() {
        if (userService != null) {
            User wrappedUser = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElse(null);
            this.permissionScope = definePermissionScopeFor(this, wrappedUser);
        }
    }

    public void setContent(List<ContentBlock> content) {
        this.content.clear();
        this.content.addAll(content);
    }

    public List<String> getLikedUsernames() {
        return likes.stream()
                .map(ArticleLike::getUser)
                .map(User::getUsername)
                .toList();
    }

    public List<Long> getCommentsIds() {
        return comments.stream()
                .map(Comment::getId)
                .toList();
    }

    public Long getAuthorId() {
        return author.getId();
    }

    public int getLikesCount() {
        return likes.size();
    }

    public int getViewsCount() {
        return views.size();
    }

    public static List<PermissionScope> definePermissionScopeFor(Article article, User user) {
        if (user == null) return Collections.emptyList();
        if (user.getRolesNames().contains("ADMIN") || user.getId() == article.getAuthor().getId()) return List.of(PermissionScope.EDIT, PermissionScope.DELETE);

        return Collections.emptyList();
    }
}