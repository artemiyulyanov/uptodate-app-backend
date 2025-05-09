package me.artemiyulyanov.uptodate.controllers.api.faq;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.artemiyulyanov.uptodate.controllers.AuthenticatedController;
import me.artemiyulyanov.uptodate.models.FaqItem;
import me.artemiyulyanov.uptodate.models.FaqSection;
import me.artemiyulyanov.uptodate.models.Role;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.services.FaqService;
import me.artemiyulyanov.uptodate.web.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/faq")
@Tag(name = "FAQ", description = "Endpoints to interact with FAQ")
public class FaqController extends AuthenticatedController {
    @Autowired
    private RequestService requestService;

    @Autowired
    private FaqService faqService;

    @Operation(summary = "Gets a FAQ item")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "The FAQ item is not found!"),
            @ApiResponse(responseCode = "200", description = "The FAQ item has been retrieved successfully!")
    })
    @GetMapping("/items/{id}")
    public ResponseEntity<?> getFaqItemById(
            @Parameter(name = "The ID of FAQ item to be found")
            @PathVariable
            Long id
    ) {
        Optional<FaqItem> item = faqService.getFaqItemById(id);
        if (item.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The FAQ item is not found!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The FAQ item has been retrieved successfully!", item.get());
    }

    @Operation(summary = "Deletes a FAQ item")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "The FAQ item is not found!"),
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "200", description = "The FAQ item has been deleted successfully!")
    })
    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> deleteFaqItem(
            @Parameter(name = "The ID of FAQ item to be deleted")
            @PathVariable
            Long id
    ) {
        if (!faqService.itemExists(id)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The FAQ item is not found!");
        }

        faqService.deleteFaqItem(id);
        return requestService.executeApiResponse(HttpStatus.OK, "The FAQ item has been deleted successfully!");
    }

    @Operation(summary = "Gets all the existing FAQ sections")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The FAQ sections have been retrieved successfully!")
    })
    @GetMapping("/sections")
    public ResponseEntity<?> getAllFaqSections() {
        List<FaqSection> sections = faqService.getAllFaqSections();
        return requestService.executeEntityResponse(HttpStatus.OK, "The FAQ sections have been retrieved successfully!", sections);
    }

    @Operation(summary = "Creates a new FAQ section")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "403", description = "The user is not allowed to create a new FAQ section"),
            @ApiResponse(responseCode = "200", description = "The FAQ section has been created successfully!")
    })
    @PostMapping("/sections")
    public ResponseEntity<?> createFaqSection(
            @Parameter(name = "The title of FAQ section")
            @RequestParam
            String title
    ) {
        Optional<User> wrappedUser = getAuthorizedUser();

        if (wrappedUser.isEmpty() || wrappedUser.get().getRoles().stream().noneMatch(role -> role.getName().equalsIgnoreCase("ADMIN"))) {
            return requestService.executeApiResponse(HttpStatus.FORBIDDEN, "You are not allowed to perform this action!");
        }

        FaqSection section = faqService.createFaqSection(title);
        return requestService.executeEntityResponse(HttpStatus.OK, "The FAQ section has been created successfully!", section);
    }

    @Operation(summary = "Gets FAQ items by section")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "The FAQ section is not found!"),
            @ApiResponse(responseCode = "200", description = "The FAQ items have been retrieved successfully!")
    })
    @GetMapping("/sections/{sectionId}")
    public ResponseEntity<?> getAllFaqItemsBySection(
            @Parameter(name = "The ID of FAQ section whose items are supposed to be retrieved")
            @PathVariable
            Long sectionId
    ) {
        if (!faqService.sectionExists(sectionId)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The FAQ section is not found!");
        }

        List<FaqItem> items = faqService.getFaqSectionById(sectionId).get().getItems();
        return requestService.executeEntityResponse(HttpStatus.OK, "The FAQ items have been retrieved successfully!", items);
    }

    @Operation(summary = "Creates a new FAQ item in a section")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "The FAQ section is not found!"),
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "200", description = "The FAQ item has been created successfully!")
    })
    @PostMapping("/sections/{sectionId}")
    public ResponseEntity<?> createFaqItem(
            @Parameter(name = "The ID of section")
            @PathVariable
            Long sectionId,
            @Parameter(name = "The title of item")
            @RequestParam
            String title,
            @Parameter(name = "The content of item")
            @RequestParam
            String content,
            @Parameter(description = "The additional resources of FAQ item (e.g. images)")
            @RequestParam(required = false)
            List<MultipartFile> resources
    ) {
        Optional<FaqSection> wrappedSection = faqService.getFaqSectionById(sectionId);
        if (wrappedSection.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The FAQ section is not found!");
        }

        FaqItem item = faqService.createFaqItem(title, content, wrappedSection.get(), resources);
        return requestService.executeEntityResponse(HttpStatus.OK, "The FAQ item has been created successfully!", item);
    }

    @Operation(summary = "Deletes a FAQ section")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "The FAQ section is not found!"),
            @ApiResponse(responseCode = "401", description = "The user is unauthorized"),
            @ApiResponse(responseCode = "200", description = "The FAQ section has been deleted successfully!")
    })
    @DeleteMapping("/sections/{id}")
    public ResponseEntity<?> deleteFaqSection(
            @Parameter(name = "The ID of FAQ section")
            @PathVariable
            Long id
    ) {
        if (!faqService.sectionExists(id)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The FAQ section is not found!");
        }

        faqService.deleteFaqSection(id);
        return requestService.executeApiResponse(HttpStatus.OK, "The FAQ section has been deleted successfully!");
    }
}
