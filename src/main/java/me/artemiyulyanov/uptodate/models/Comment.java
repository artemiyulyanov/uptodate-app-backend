package me.artemiyulyanov.uptodate.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import me.artemiyulyanov.uptodate.services.CommentService;
import me.artemiyulyanov.uptodate.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "comments")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Setter
    private static CommentService commentService;

    @Setter
    private static UserService userService;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> likes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User author;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "comment_resources", joinColumns = @JoinColumn(name = "comment_id"))
    @Column(name = "file")
    private List<String> resources = new ArrayList<>();

    @Transient
    @Builder.Default
    private List<PermissionScope> permissionScope = new ArrayList<>();

    @PostLoad
    @PrePersist
    private void initResources() {
        if (userService != null) {
            User wrappedUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElse(null);
            this.permissionScope = definePermissionScopeFor(this, wrappedUser);
        }
    }

    public Long getArticleId() {
        return article.getId();
    }

    public Long getAuthorId() {
        return author.getId();
    }

    public List<String> getLikedUsernames() {
        return likes.stream()
                .map(comment -> comment.getUser().getUsername())
                .toList();
    }

    public static List<PermissionScope> definePermissionScopeFor(Comment comment, User user) {
        if (user == null) return Collections.emptyList();
        if (user.getRolesNames().contains("ADMIN") || user.getId() == comment.getAuthor().getId()) return List.of(PermissionScope.EDIT, PermissionScope.DELETE);

        return Collections.emptyList();
    }
}