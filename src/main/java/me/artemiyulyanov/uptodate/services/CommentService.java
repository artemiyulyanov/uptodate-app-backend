package me.artemiyulyanov.uptodate.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.artemiyulyanov.uptodate.minio.MinioService;
import me.artemiyulyanov.uptodate.minio.resources.CommentResourceManager;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.models.Comment;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService implements ResourceService<CommentResourceManager> {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MinioService minioService;

    @Autowired
    private ObjectMapper objectMapper;

    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    public List<Comment> findAllById(List<Long> ids) {
        return commentRepository.findAllById(ids);
    }

    public List<Comment> findByArticle(Article article) {
        return commentRepository.findByArticle(article);
    }

    public List<Comment> findByAuthor(User author) {
        return commentRepository.findByAuthor(author);
    }

    @Transactional
    public Comment create(String content, User author, Article article, List<MultipartFile> resources) {
        Comment comment = Comment.builder()
                .content(content)
                .createdAt(LocalDateTime.now())
                .author(author)
                .article(article)
                .build();

        comment = commentRepository.save(comment);
        List<String> resourcesUrls = getResourceManager().uploadResources(comment, resources);

        comment.setResources(resourcesUrls);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment edit(Long id, String content, List<MultipartFile> resources) {
        Comment newComment = commentRepository.findById(id).get();

        newComment.setContent(content);

        List<String> updatedResources = getResourceManager().updateResources(newComment, resources);
        newComment.setResources(updatedResources);
        return commentRepository.save(newComment);
    }

    @Transactional
    public void delete(Comment comment) {
        getResourceManager().deleteResources(comment);
        commentRepository.delete(comment);
    }

    @Transactional
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public CommentResourceManager getResourceManager() {
        return CommentResourceManager
                .builder()
                .commentRepository(commentRepository)
                .minioService(minioService)
                .build();
    }
}