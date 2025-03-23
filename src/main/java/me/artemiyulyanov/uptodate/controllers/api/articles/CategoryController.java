package me.artemiyulyanov.uptodate.controllers.api.articles;

import me.artemiyulyanov.uptodate.models.Category;
import me.artemiyulyanov.uptodate.web.RequestService;
import me.artemiyulyanov.uptodate.services.ArticleService;
import me.artemiyulyanov.uptodate.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RequestService requestService;

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        return requestService.executeEntityResponse(HttpStatus.OK, "The request has been proceeded successfully!", categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        Optional<Category> wrappedCategory = categoryService.findById(id);

        if (wrappedCategory.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Article category is undefined!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The request has been proceeded successfully!", wrappedCategory.get());
    }
}