package me.artemiyulyanov.uptodate.controllers.api.faq;

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
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/faq")
public class FaqController extends AuthenticatedController {
    @Autowired
    private RequestService requestService;

    @Autowired
    private FaqService faqService;

    @GetMapping("/items/{id}")
    public ResponseEntity<?> getFaqItemById(
            @PathVariable Long id
    ) {
        Optional<FaqItem> item = faqService.getFaqItemById(id);
        if (item.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The FAQ item is not found!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The FAQ item has been retrieved successfully!", item.get());
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> deleteFaqItem(
            @PathVariable Long id
    ) {
        if (!faqService.itemExists(id)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The FAQ item is not found!");
        }

        faqService.deleteFaqItem(id);
        return requestService.executeApiResponse(HttpStatus.OK, "The FAQ item has been deleted successfully!");
    }

    @GetMapping("/sections")
    public ResponseEntity<?> getAllFaqSections() {
        List<FaqSection> sections = faqService.getAllFaqSections();
        return requestService.executeEntityResponse(HttpStatus.OK, "The FAQ sections have been retrieved successfully!", sections);
    }

    @PostMapping("/sections")
    public ResponseEntity<?> createFaqSection(
            @RequestParam String title
    ) {
        Optional<User> wrappedUser = getAuthorizedUser();

        if (wrappedUser.isEmpty() || wrappedUser.get().getRoles().stream().noneMatch(role -> role.getName().equalsIgnoreCase("ADMIN"))) {
            return requestService.executeApiResponse(HttpStatus.FORBIDDEN, "You are not allowed to perform this action!");
        }

        FaqSection section = faqService.createFaqSection(title);
        return requestService.executeEntityResponse(HttpStatus.OK, "The FAQ section has been created successfully!", section);
    }

    @GetMapping("/sections/{sectionId}")
    public ResponseEntity<?> getAllFaqItemsBySection(
            @PathVariable Long sectionId
    ) {
        if (!faqService.sectionExists(sectionId)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The FAQ section is not found!");
        }

        List<FaqItem> items = faqService.getFaqSectionById(sectionId).get().getItems();
        return requestService.executeEntityResponse(HttpStatus.OK, "The FAQ section has been retrieved successfully!", items);
    }

    @PostMapping("/sections/{sectionId}")
    public ResponseEntity<?> createFaqItem(
            @PathVariable Long sectionId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) List<MultipartFile> resources
    ) {
        Optional<FaqSection> wrappedSection = faqService.getFaqSectionById(sectionId);
        if (wrappedSection.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The FAQ section is not found!");
        }

        FaqItem item = faqService.createFaqItem(title, content, wrappedSection.get(), resources);
        return requestService.executeEntityResponse(HttpStatus.OK, "The FAQ item has been created successfully!", item);
    }

    @DeleteMapping("/sections/{sectionId}")
    public ResponseEntity<?> deleteFaqSection(
            @PathVariable Long sectionId
    ) {
        if (!faqService.sectionExists(sectionId)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The FAQ section is not found!");
        }

        faqService.deleteFaqSection(sectionId);
        return requestService.executeApiResponse(HttpStatus.OK, "The FAQ section has been deleted successfully!");
    }
}
