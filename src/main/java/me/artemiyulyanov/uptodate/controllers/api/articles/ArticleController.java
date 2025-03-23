package me.artemiyulyanov.uptodate.controllers.api.articles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @GetMapping
    public ResponseEntity<?> getArticlesByIds(@RequestParam(defaultValue = "", required = false) List<Long> ids) {
        List<Article> articles = articleService.findAllById(ids);
        return requestService.executeEntityResponse(HttpStatus.OK, "The articles have been retrieved successfully!", articles);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getArticleBySlug(
            @PathVariable String slug) {
        Optional<Article> wrappedArticle = articleService.findBySlug(slug);

        if (wrappedArticle.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Article is undefined!");
        }

        articleViewService.view(wrappedArticle.get(), getAuthorizedUser().orElse(null));
        return requestService.executeEntityResponse(HttpStatus.OK, "The request has been proceeded successfully!", wrappedArticle.get());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getArticleById(@PathVariable Long id) {
        Optional<Article> wrappedArticle = articleService.findById(id);

        if (wrappedArticle.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Article is undefined!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The request has been proceeded successfully!", wrappedArticle.get());
    }

    @PatchMapping("/{id}/like")
    public ResponseEntity<?> likeArticle(@PathVariable Long id, Model model) {
        Optional<User> wrappedUser = getAuthorizedUser();

        Optional<Article> wrappedArticle = articleService.findById(id);
        if (wrappedArticle.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The article is undefined!");
        }

        Article updatedArticle = articleLikeService.like(wrappedArticle.get(), wrappedUser.get());
        return requestService.executeEntityResponse(HttpStatus.OK, "The article has been liked by the user successfully!", updatedArticle);
    }

    @PostMapping
    public ResponseEntity<?> createArticle(
            @RequestParam String heading,
            @RequestParam String description,
            @RequestParam String content,
            @RequestParam List<String> topicsNames,
            @RequestParam MultipartFile cover,
            @RequestParam(required = false) List<MultipartFile> resources) {
        Optional<User> wrappedUser = getAuthorizedUser();

        if (resources == null) {
            resources = Collections.emptyList();
        }

        Article createdArticle = articleService.create(wrappedUser.get(), heading, description, content, topicsNames, cover, resources);
        if (createdArticle == null) {
            return requestService.executeApiResponse(HttpStatus.EXPECTATION_FAILED, "Unable to create article!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The article has been created!", createdArticle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editArticle(
            @PathVariable Long id,
            @RequestParam String heading,
            @RequestParam String description,
            @RequestParam String content,
            @RequestParam List<String> topicsNames,
            @RequestParam(required = false) MultipartFile cover,
            @RequestParam(required = false) List<MultipartFile> resources) {
        Optional<Article> wrappedArticle = articleService.findById(id);

        if (wrappedArticle.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Article is undefined!");
        }

        Article article = wrappedArticle.get();
        if (!article.getPermissionScope().contains(PermissionScope.EDIT)) {
            return requestService.executeApiResponse(HttpStatus.FORBIDDEN, "The authorized user has no authority to proceed the changes!");
        }

        Article updatedArticle = articleService.edit(id, heading, description, content, topicsNames, cover, resources);
        if (updatedArticle == null) {
            return requestService.executeApiResponse(HttpStatus.EXPECTATION_FAILED, "Unable to edit article!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The changes have been applied successfully!", updatedArticle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
        Optional<User> wrappedUser = getAuthorizedUser();
        Optional<Article> wrappedArticle = articleService.findById(id);

        if (wrappedArticle.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Article is undefined!");
        }

        Article article = wrappedArticle.get();
        if (!article.getPermissionScope().contains(PermissionScope.DELETE)) {
            return requestService.executeApiResponse(HttpStatus.FORBIDDEN, "The authorized user has no authority to proceed the removal!");
        }

        articleService.delete(article);
        return requestService.executeApiResponse(HttpStatus.OK, "The removal has been processed successfully!");
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchArticles(
            @RequestParam(defaultValue = "1", required = false) Integer page,
            @RequestParam(required = false) Integer pages,
            @RequestParam String query,
            @RequestParam(value = "filters") String filtersRow) throws JsonProcessingException, UnsupportedEncodingException {
        HashMap<String, Object> filters = objectMapper.readValue(URLDecoder.decode(filtersRow, "UTF-8"), new TypeReference<>() {});
        filters.put("query", query);

        PageableObject<Article> pageableObject;

        if (pages != null) {
            pageableObject = PageableObject.of(Article.class, 0, pages * ARTICLE_PAGE_SIZE);
        } else {
            pageableObject = PageableObject.of(Article.class, page - 1, ARTICLE_PAGE_SIZE);
        }

        Page<Article> paginatedArticles = articleService.findAllArticles(
                ArticleFilter.applyFilters(
                        pageableObject,
                        filters
                )
        );

        return requestService.executePaginatedEntityResponse(HttpStatus.OK, paginatedArticles);
    }
}