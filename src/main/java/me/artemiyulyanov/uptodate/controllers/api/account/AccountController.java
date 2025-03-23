package me.artemiyulyanov.uptodate.controllers.api.account;

import me.artemiyulyanov.uptodate.controllers.AuthenticatedController;
import me.artemiyulyanov.uptodate.controllers.api.account.responses.ChangesAvailableResponse;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.models.UserSettings;
import me.artemiyulyanov.uptodate.repositories.ArticleLikeRepository;
import me.artemiyulyanov.uptodate.repositories.UserSettingsRepository;
import me.artemiyulyanov.uptodate.services.ArticleLikeService;
import me.artemiyulyanov.uptodate.services.ArticleViewService;
import me.artemiyulyanov.uptodate.services.UserService;
import me.artemiyulyanov.uptodate.web.RequestService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/account")
public class AccountController extends AuthenticatedController {
    @Autowired
    private UserService userService;

    @Autowired
    private ArticleViewService articleViewService;

    @Autowired
    private ArticleLikeService articleLikeService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @GetMapping
    public ResponseEntity<?> me() {
        Optional<User> wrappedUser = getAuthorizedUser();
        return requestService.executeEntityResponse(HttpStatus.OK, "The user information has been retrieved successfully!", wrappedUser.get());
    }

    @GetMapping("/changes-available")
    public ResponseEntity<?> checkChangesAvailable(
            @RequestParam String username,
            @RequestParam String email
    ) {
        Optional<User> wrappedUser = getAuthorizedUser();
        List<ChangesAvailableResponse.ConflictedColumn> conflictedColumns = userService.getConflictedColumnsWhileEditing(wrappedUser.get(), email, username);

        return requestService.executeCustomResponse(
                ChangesAvailableResponse.builder()
                        .changesAvailable(conflictedColumns.isEmpty())
                        .conflictedColumns(conflictedColumns)
                        .status(200)
                        .build()
        );
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> statistics() {
        Optional<User> wrappedUser = getAuthorizedUser();
        return requestService.executeEntityResponse(HttpStatus.OK, "The statistics has been retrieved successfully!", wrappedUser.get().getStatistics());
    }

    @PutMapping
    public ResponseEntity<?> editAccount(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String username,
            @RequestBody UserSettings settings) {
        User user = getAuthorizedUser().get();

        if (!userService.getConflictedColumnsWhileEditing(user, user.getEmail(), username).isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.CONFLICT, "The username is already taken!");
        }

        User updatedUser = userService.edit(user.getId(), username, firstName, lastName, settings);
        return requestService.executeEntityResponse(HttpStatus.OK, "The changes have been applied successfully!", updatedUser);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAccount() {
        User user = getAuthorizedUser().get();
        userService.delete(user);

        return requestService.executeApiResponse(HttpStatus.OK, "The user has been deleted successfully!");
    }

    @GetMapping("/settings")
    public ResponseEntity<?> settings() {
        User user = getAuthorizedUser().get();
        return requestService.executeEntityResponse(HttpStatus.OK, "The settings have been retrieved successfully!", user.getSettings());
    }
}