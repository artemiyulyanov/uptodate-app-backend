package me.artemiyulyanov.uptodate.controllers.api.articles;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Сategories", description = "Endpoints to interact with categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RequestService requestService;

    @GetMapping
    @Operation(summary = "Gets all the categories")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The categories have been retrieved successfully!")
    })
    public ResponseEntity<?> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return requestService.executeEntityResponse(HttpStatus.OK, "The request has been proceeded successfully!", categories);
    }

    @Operation(summary = "Gets category by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "The category is not found!"),
            @ApiResponse(responseCode = "200", description = "The category has been retrieved successfully!")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(
            @Parameter(description = "The ID of the category to be found")
            @PathVariable
            Long id
    ) {
        Optional<Category> wrappedCategory = categoryService.getCategoryById(id);

        if (wrappedCategory.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Article category is undefined!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The request has been proceeded successfully!", wrappedCategory.get());
    }
}