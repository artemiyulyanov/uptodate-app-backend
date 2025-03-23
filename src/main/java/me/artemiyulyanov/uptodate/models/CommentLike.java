package me.artemiyulyanov.uptodate.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "comments_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"comment_id", "user_id"})
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    @JsonIgnore
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime likedAt;

    public Long getCommentId() {
        return comment.getId();
    }

    public Long getUserId() {
        return user.getId();
    }
}