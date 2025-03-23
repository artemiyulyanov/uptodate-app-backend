package me.artemiyulyanov.uptodate.controllers.api.articles;

import me.artemiyulyanov.uptodate.controllers.AuthenticatedController;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.models.Comment;
import me.artemiyulyanov.uptodate.models.PermissionScope;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.services.CommentLikeService;
import me.artemiyulyanov.uptodate.services.CommentService;
import me.artemiyulyanov.uptodate.services.ArticleService;
import me.artemiyulyanov.uptodate.services.UserService;
import me.artemiyulyanov.uptodate.web.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController extends AuthenticatedController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentLikeService commentLikeService;

    @Autowired
    private RequestService requestService;

    @GetMapping
    public ResponseEntity<?> getCommentsByIds(@RequestParam(defaultValue = "", required = false) List<Long> ids) {
        List<Comment> comments = commentService.findAllById(ids);
        return requestService.executeEntityResponse(HttpStatus.OK, "The comments have been retrieved successfully!", comments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable Long id) {
        Optional<Comment> comment = commentService.findById(id);

        if (comment.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Comment is undefined!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The comment has been retrieved successfully!", comment.get());
    }

    @GetMapping("/author/{id}")
    public ResponseEntity<?> getCommentsByAuthor(@PathVariable Long id) {
        Optional<User> wrappedAuthor = userService.findById(id);

        if (!wrappedAuthor.isPresent()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Author is undefined!");
        }

        List<Comment> comments = commentService.findByAuthor(wrappedAuthor.get());
        return requestService.executeEntityResponse(HttpStatus.OK, "The request has been proceeded successfully!", comments);
    }

    @GetMapping("/article/{id}")
    public ResponseEntity<?> getCommentsByArticle(@PathVariable Long id) {
        Optional<Article> wrappedArticle = articleService.findById(id);

        if (!wrappedArticle.isPresent()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Comment is undefined!");
        }

        List<Comment> comments = commentService.findByArticle(wrappedArticle.get());
        return requestService.executeEntityResponse(HttpStatus.OK, "The request has been proceeded successfully!", comments);
    }

    @PostMapping("/{articleId}")
    public ResponseEntity<?> createComment(
            @PathVariable Long articleId,
            @RequestParam String content,
            @RequestParam(value = "resources", required = false) List<MultipartFile> resources) {
        Optional<User> wrappedUser = getAuthorizedUser();
        Optional<Article> wrappedArticle = articleService.findById(articleId);

        if (wrappedArticle.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The article is undefined");
        }

        Comment createdComment = commentService.create(content, wrappedUser.get(), wrappedArticle.get(), resources);

        if (createdComment == null) {
            return requestService.executeApiResponse(HttpStatus.EXPECTATION_FAILED, "Unable to create comment!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The comment has been created!", createdComment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editComment(
            @PathVariable Long id,
            @RequestParam String content,
            @RequestParam(value = "resources", required = false) List<MultipartFile> resources) {
        Optional<Comment> wrappedArticleComment = commentService.findById(id);

        if (wrappedArticleComment.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Comment is undefined!");
        }

        Comment comment = wrappedArticleComment.get();
        if (!comment.getPermissionScope().contains(PermissionScope.EDIT)) {
            return requestService.executeApiResponse(HttpStatus.FORBIDDEN, "The authorized user has no authority to proceed the changes!");
        }

        Comment updatedComment = commentService.edit(id, content, resources);
        if (updatedComment == null) {
            return requestService.executeApiResponse(HttpStatus.EXPECTATION_FAILED, "Unable to edit comment!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The changes have been applied successfully!", updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        Optional<User> wrappedUser = getAuthorizedUser();
        Optional<Comment> wrappedArticleComment = commentService.findById(id);

        if (wrappedArticleComment.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Comment is undefined!");
        }

        Comment comment = wrappedArticleComment.get();
        if (!comment.getPermissionScope().contains(PermissionScope.DELETE)) {
            return requestService.executeApiResponse(HttpStatus.FORBIDDEN, "The authorized user has no authority to proceed the removal!");
        }

        commentService.delete(comment);
        return requestService.executeApiResponse(HttpStatus.OK, "The removal has been processed successfully!");
    }

    @PatchMapping("/{id}/like")
    public ResponseEntity<?> likeComment(@PathVariable Long id) {
        Optional<User> wrappedUser = getAuthorizedUser();

        Optional<Comment> wrappedComment = commentService.findById(id);
        if (wrappedComment.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The comment is undefined!");
        }

        Comment comment = commentLikeService.like(wrappedComment.get(), wrappedUser.get());
        return requestService.executeEntityResponse(HttpStatus.OK, "The comment has been liked by the user successfully!", comment);
    }
}