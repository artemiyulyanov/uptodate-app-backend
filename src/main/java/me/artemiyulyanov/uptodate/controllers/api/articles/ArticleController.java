package me.artemiyulyanov.uptodate.controllers.api.articles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.links.LinkParameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.artemiyulyanov.uptodate.controllers.AuthenticatedController;
import me.artemiyulyanov.uptodate.controllers.api.articles.filters.ArticleFilter;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.models.PermissionScope;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.services.*;
import me.artemiyulyanov.uptodate.web.PageableObject;
import me.artemiyulyanov.uptodate.web.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/articles")
@Tag(name = "Articles", description = "Endpoints to interact with articles")
public class ArticleController extends AuthenticatedController {
    public static final int ARTICLE_PAGE_SIZE = 2;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleViewService articleViewService;

    @Autowired
    private ArticleLikeService articleLikeService;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Operation(summary = "Get articles by their IDs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The articles have been retrieved successfully!")
    })
    @GetMapping
    public ResponseEntity<?> getArticlesByIds(
            @Parameter(description = "The IDs of articles to be found")
            @RequestParam(defaultValue = "", required = false)
            List<Long> ids
    ) {
        List<Article> articles = articleService.getAllArticles(ids);
        return requestService.executeEntityResponse(HttpStatus.OK, "The articles have been retrieved successfully!", articles);
    }

    @Operation(summary = "Gets an article by its slug")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Article is not found!"),
            @ApiResponse(responseCode = "200", description = "The article has been retrieved successfully!")
    })
    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getArticleBySlug(
            @Parameter(description = "The slug of article to be found")
            @PathVariable String slug
    ) {
        Optional<Article> wrappedArticle = articleService.getArticleBySlug(slug);

        if (wrappedArticle.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Article is undefined!");
        }

        articleViewService.view(wrappedArticle.get(), getAuthorizedUser().orElse(null));
        return requestService.executeEntityResponse(HttpStatus.OK, "The request has been proceeded successfully!", wrappedArticle.get());
    }

    @Operation(summary = "Gets an article by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Article is not found!"),
            @ApiResponse(responseCode = "200", description = "The article has been retrieved successfully!")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getArticleById(
            @Parameter(description = "The ID of article to be found")
            @PathVariable Long id
    ) {
        Optional<Article> wrappedArticle = articleService.getArticleById(id);

        if (wrappedArticle.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Article is undefined!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The request has been proceeded successfully!", wrappedArticle.get());
    }

    @Operation(summary = "Likes/unlikes article")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Article is not found!"),
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "200", description = "The article has been liked/unliked successfully!")
    })
    @PatchMapping("/{id}/like")
    public ResponseEntity<?> likeArticle(
            @Parameter(description = "The ID of article to be liked/unliked")
            @PathVariable Long id
    ) {
        Optional<User> wrappedUser = getAuthorizedUser();

        Optional<Article> wrappedArticle = articleService.getArticleById(id);
        if (wrappedArticle.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The article is undefined!");
        }

        Article updatedArticle = articleLikeService.likeArticle(wrappedArticle.get(), wrappedUser.get());
        return requestService.executeEntityResponse(HttpStatus.OK, "The article has been liked by the user successfully!", updatedArticle);
    }

    @Operation(summary = "Creates a new article")
    @ApiResponses({
            @ApiResponse(responseCode = "417", description = "Unable to create an article!"),
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "200", description = "The article has been created successfully!")
    })
    @PostMapping
    public ResponseEntity<?> createArticle(
            @Parameter(description = "The heading of article")
            @RequestParam
            String heading,
            @Parameter(description = "The short description of article")
            @RequestParam
            String description,
            @Parameter(description = "The content of article (consists of large texts, lists, images etc.)")
            @RequestParam
            String content,
            @Parameter(description = "The topics of article")
            @RequestParam
            List<String> topicsNames,
            @Parameter(description = "The cover of article")
            @RequestParam(required = false)
            MultipartFile cover,
            @Parameter(description = "The additional resources of article (e.g. images)")
            @RequestParam(required = false)
            List<MultipartFile> resources) {
        Optional<User> wrappedUser = getAuthorizedUser();

        if (resources == null) {
            resources = Collections.emptyList();
        }

        Article createdArticle = articleService.createArticle(wrappedUser.get(), heading, description, content, topicsNames, cover, resources);
        if (createdArticle == null) {
            return requestService.executeApiResponse(HttpStatus.EXPECTATION_FAILED, "Unable to create an article!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The article has been created!", createdArticle);
    }

    @Operation(summary = "Fully replaces the existing article")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "The article is not found!"),
            @ApiResponse(responseCode = "403", description = "No eligibility to update an article"),
            @ApiResponse(responseCode = "417", description = "Unable to update article!"),
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "200", description = "The article has been created successfully!")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> editArticle(
            @Parameter(description = "The ID of article")
            @PathVariable
            Long id,
            @Parameter(description = "The heading of article")
            @RequestParam
            String heading,
            @Parameter(description = "The short description of article")
            @RequestParam
            String description,
            @Parameter(description = "The content of article (consists of large texts, lists, images etc.)")
            @RequestParam
            String content,
            @Parameter(description = "The topics of article")
            @RequestParam
            List<String> topicsNames,
            @Parameter(description = "The cover of article")
            @RequestParam
            MultipartFile cover,
            @Parameter(description = "The additional resources of article (e.g. images)")
            @RequestParam(required = false)
            List<MultipartFile> resources
    ) {
        Optional<Article> wrappedArticle = articleService.getArticleById(id);

        if (wrappedArticle.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Article is undefined!");
        }

        Article article = wrappedArticle.get();
        if (!article.getPermissionScope().contains(PermissionScope.EDIT)) {
            return requestService.executeApiResponse(HttpStatus.FORBIDDEN, "The authorized user has no authority to proceed the changes!");
        }

        Article updatedArticle = articleService.editArticle(id, heading, description, content, topicsNames, cover, resources);
        if (updatedArticle == null) {
            return requestService.executeApiResponse(HttpStatus.EXPECTATION_FAILED, "Unable to edit an article!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The changes have been applied successfully!", updatedArticle);
    }

    @Operation(summary = "Deletes the existing article")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "The article is not found!"),
            @ApiResponse(responseCode = "403", description = "No eligibility to delete article"),
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "200", description = "The article has been created successfully!")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
        Optional<User> wrappedUser = getAuthorizedUser();
        Optional<Article> wrappedArticle = articleService.getArticleById(id);

        if (wrappedArticle.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Article is undefined!");
        }

        Article article = wrappedArticle.get();
        if (!article.getPermissionScope().contains(PermissionScope.DELETE)) {
            return requestService.executeApiResponse(HttpStatus.FORBIDDEN, "The authorized user has no authority to proceed the removal!");
        }

        articleService.deleteArticle(article);
        return requestService.executeApiResponse(HttpStatus.OK, "The removal has been processed successfully!");
    }

    @Operation(summary = "Searches paginated articles by applied conditions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The articles have been created successfully!")
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchArticles(
            @Parameter(description = "The selected page of elements (equals by default 1)")
            @RequestParam(defaultValue = "1", required = false)
            Integer page,
            @Parameter(description = "Gets first n pages")
            @RequestParam(required = false)
            Integer pages,
            @Parameter(description = "Search query (applies to heading, content, description, author etc.)")
            @RequestParam(defaultValue = "")
            String query,
            @Parameter(description = "Search filters (required as a json)")
            @RequestParam(value = "filters", defaultValue = "[]")
            String filtersRow
    ) throws JsonProcessingException, UnsupportedEncodingException {
        HashMap<String, Object> filters = objectMapper.readValue(URLDecoder.decode(filtersRow, "UTF-8"), new TypeReference<>() {});
        filters.put("query", query);

        PageableObject<Article> pageableObject;

        if (pages != null) {
            pageableObject = PageableObject.of(Article.class, 0, pages * ARTICLE_PAGE_SIZE);
        } else {
            pageableObject = PageableObject.of(Article.class, page - 1, ARTICLE_PAGE_SIZE);
        }

        Page<Article> paginatedArticles = articleService.getAllArticles(
                ArticleFilter.applyFilters(
                        pageableObject,
                        filters
                )
        );

        return requestService.executePaginatedEntityResponse(HttpStatus.OK, paginatedArticles);
    }
}