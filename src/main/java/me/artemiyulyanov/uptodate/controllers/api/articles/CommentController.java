package me.artemiyulyanov.uptodate.controllers.api.articles;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Comments", description = "Endpoints to interact with comments")
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

    @Operation(summary = "Gets all the comments by their IDs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The comments have been retrieved successfully!")
    })
    @GetMapping
    public ResponseEntity<?> getCommentsByIds(
            @Parameter(description = "The IDs of the comments to be found")
            @RequestParam(defaultValue = "", required = false)
            List<Long> ids
    ) {
        List<Comment> comments = commentService.getAllComments(ids);
        return requestService.executeEntityResponse(HttpStatus.OK, "The comments have been retrieved successfully!", comments);
    }

    @Operation(summary = "Gets comment by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "The comment is not found!"),
            @ApiResponse(responseCode = "200", description = "The comments have been retrieved successfully!")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(
            @Parameter(description = "The ID of the comment to be found")
            @PathVariable
            Long id
    ) {
        Optional<Comment> comment = commentService.getCommentById(id);

        if (comment.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Comment is undefined!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The comment has been retrieved successfully!", comment.get());
    }

    @Operation(summary = "Gets comments by the ID of the author")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "The comment is not found!"),
            @ApiResponse(responseCode = "200", description = "The comments have been retrieved successfully!")
    })
    @GetMapping("/author/{id}")
    public ResponseEntity<?> getCommentsByAuthor(
            @Parameter(description = "The ID of the author whose comments are supposed to be found")
            @PathVariable
            Long id
    ) {
        Optional<User> wrappedAuthor = userService.getUserById(id);

        if (!wrappedAuthor.isPresent()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Author is undefined!");
        }

        List<Comment> comments = commentService.getCommentByAuthor(wrappedAuthor.get());
        return requestService.executeEntityResponse(HttpStatus.OK, "The request has been proceeded successfully!", comments);
    }

    @Operation(summary = "Gets the comments of the article")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "The article is not found!"),
            @ApiResponse(responseCode = "200", description = "The comments have been retrieved successfully!")
    })
    @GetMapping("/article/{id}")
    public ResponseEntity<?> getCommentsByArticle(
            @Parameter(description = "The ID of the article whose comments are supposed to be found")
            @PathVariable
            Long id
    ) {
        Optional<Article> wrappedArticle = articleService.getArticleById(id);

        if (!wrappedArticle.isPresent()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The article is undefined!");
        }

        List<Comment> comments = commentService.getCommentByArticle(wrappedArticle.get());
        return requestService.executeEntityResponse(HttpStatus.OK, "The request has been proceeded successfully!", comments);
    }

    @Operation(summary = "Creates a new comment to article")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "The article is not found!"),
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "417", description = "Unable to create a comment!"),
            @ApiResponse(responseCode = "200", description = "The comment has been created successfully!")
    })
    @PostMapping("/{articleId}")
    public ResponseEntity<?> createComment(
            @Parameter(description = "The ID of the article to whom this comment concern")
            @PathVariable
            Long articleId,
            @Parameter(description = "The content of the comment")
            @RequestParam
            String content,
            @Parameter(description = "The additional resources of comment (e.g. images)")
            @RequestParam(value = "resources", required = false)
            List<MultipartFile> resources
    ) {
        Optional<User> wrappedUser = getAuthorizedUser();
        Optional<Article> wrappedArticle = articleService.getArticleById(articleId);

        if (wrappedArticle.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The article is undefined");
        }

        Comment createdComment = commentService.createComment(content, wrappedUser.get(), wrappedArticle.get(), resources);

        if (createdComment == null) {
            return requestService.executeApiResponse(HttpStatus.EXPECTATION_FAILED, "Unable to create a comment!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The comment has been created!", createdComment);
    }

    @Operation(summary = "Updates a comment by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "The comment is not found!"),
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "403", description = "No eligibility to update a comment"),
            @ApiResponse(responseCode = "417", description = "Unable to update a comment!"),
            @ApiResponse(responseCode = "200", description = "The comment has been updated successfully!")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> editComment(
            @Parameter(name = "The ID of the comment to be edited")
            @PathVariable
            Long id,
            @Parameter(name = "The new content of the comment")
            @RequestParam
            String content,
            @Parameter(name = "The additional resources of the comment (e.g. images)")
            @RequestParam(value = "resources", required = false)
            List<MultipartFile> resources
    ) {
        Optional<Comment> wrappedArticleComment = commentService.getCommentById(id);

        if (wrappedArticleComment.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Comment is undefined!");
        }

        Comment comment = wrappedArticleComment.get();
        if (!comment.getPermissionScope().contains(PermissionScope.EDIT)) {
            return requestService.executeApiResponse(HttpStatus.FORBIDDEN, "The authorized user has no authority to proceed the changes!");
        }

        Comment updatedComment = commentService.editComment(id, content, resources);
        if (updatedComment == null) {
            return requestService.executeApiResponse(HttpStatus.EXPECTATION_FAILED, "Unable to edit a comment!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The changes have been applied successfully!", updatedComment);
    }

    @Operation(summary = "Deletes a comment by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "The comment is not found!"),
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "403", description = "No eligibility to update a comment"),
            @ApiResponse(responseCode = "200", description = "The comment has been deleted successfully!")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(
            @Parameter(description = "The ID of the comment to be deleted")
            @PathVariable
            Long id
    ) {
        Optional<User> wrappedUser = getAuthorizedUser();
        Optional<Comment> wrappedArticleComment = commentService.getCommentById(id);

        if (wrappedArticleComment.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Comment is undefined!");
        }

        Comment comment = wrappedArticleComment.get();
        if (!comment.getPermissionScope().contains(PermissionScope.DELETE)) {
            return requestService.executeApiResponse(HttpStatus.FORBIDDEN, "The authorized user has no authority to proceed the removal!");
        }

        commentService.deleteComment(comment);
        return requestService.executeApiResponse(HttpStatus.OK, "The removal has been processed successfully!");
    }

    @Operation(summary = "Likes/unlikes a comment")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Comment is not found!"),
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "200", description = "The comment has been liked/unliked successfully!")
    })
    @PatchMapping("/{id}/like")
    public ResponseEntity<?> likeComment(
            @Parameter(description = "The ID of the comment to be liked/unliked")
            @PathVariable
            Long id
    ) {
        Optional<User> wrappedUser = getAuthorizedUser();

        Optional<Comment> wrappedComment = commentService.getCommentById(id);
        if (wrappedComment.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The comment is undefined!");
        }

        Comment comment = commentLikeService.likeComment(wrappedComment.get(), wrappedUser.get());
        return requestService.executeEntityResponse(HttpStatus.OK, "The comment has been liked by the user successfully!", comment);
    }
}