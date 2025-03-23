package me.artemiyulyanov.uptodate.repositories;

import me.artemiyulyanov.uptodate.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    @Query("SELECT e FROM CommentLike e WHERE e.comment.author = :author AND e.likedAt >= :after")
    List<CommentLike> findLastLikesOfAuthor(@Param("author") User author, @Param("after") LocalDateTime after);

    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);

    boolean existsByCommentAndUser(Comment comment, User user);
}