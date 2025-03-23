package me.artemiyulyanov.uptodate.services;

import me.artemiyulyanov.uptodate.models.*;
import me.artemiyulyanov.uptodate.repositories.CommentLikeRepository;
import me.artemiyulyanov.uptodate.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentLikeService {
    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private CommentService commentService;

    @Transactional
    public Comment like(Comment comment, User user) {
        List<CommentLike> likes = comment.getLikes();
        boolean alreadyLiked = likes.stream().anyMatch(like -> like.getUser().getId().equals(user.getId()));

        if (!alreadyLiked) {
            CommentLike commentLike = CommentLike
                    .builder()
                    .comment(comment)
                    .user(user)
                    .likedAt(LocalDateTime.now())
                    .build();

            likes.add(commentLike);
        } else {
            likes.removeIf(like -> like.getUser().getId().equals(user.getId()));
        }

        comment.setLikes(likes);
        return commentService.save(comment);
    }

    public List<CommentLike> findLastLikesOfAuthor(User user, LocalDateTime after) {
        return commentLikeRepository.findLastLikesOfAuthor(user, after);
    }
}