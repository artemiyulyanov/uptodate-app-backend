package me.artemiyulyanov.uptodate.controllers.api.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.security.core.parameters.P;
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
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Account", description = "Endpoints to interact with authenticated user")
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

    @Operation(summary = "Gets the information about authorized user")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "200", description = "The user information has been retrieved successfully!")
    })
    @GetMapping
    public ResponseEntity<?> me() {
        Optional<User> wrappedUser = getAuthorizedUser();
        return requestService.executeEntityResponse(HttpStatus.OK, "The user information has been retrieved successfully!", wrappedUser.get());
    }

    @Operation(summary = "Checks if the authenticated user is allowed to set new credentials", description = "Returns conflicted columns")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "200", description = "The conflicted columns of the user have been retrieved successfully!")
    })
    @GetMapping("/changes-available")
    public ResponseEntity<?> checkChangesAvailable(
            @Parameter(description = "A new username")
            @RequestParam
            String username,
            @Parameter(description = "A new email")
            @RequestParam
            String email
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

    @Operation(summary = "Gets the statistics of the authorized user")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "200", description = "The statistics has been retrieved successfully!")
    })
    @GetMapping("/statistics")
    public ResponseEntity<?> statistics() {
        Optional<User> wrappedUser = getAuthorizedUser();
        return requestService.executeEntityResponse(HttpStatus.OK, "The statistics has been retrieved successfully!", wrappedUser.get().getStatistics());
    }

    @Operation(summary = "Updates the authorized user data")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "409", description = "The user is already taken!"),
            @ApiResponse(responseCode = "417", description = "Unable to update the user!"),
            @ApiResponse(responseCode = "200", description = "The user has been updated successfully!")
    })
    @PutMapping
    public ResponseEntity<?> editAccount(
            @Parameter(description = "The first name of the user")
            @RequestParam
            String firstName,
            @Parameter(description = "The last name of the user")
            @RequestParam
            String lastName,
            @Parameter(description = "The username of the user")
            @RequestParam
            String username,
            @Parameter(description = "The settings of the user")
            @RequestBody
            UserSettings settings) {
        User user = getAuthorizedUser().get();

        if (!userService.getConflictedColumnsWhileEditing(user, user.getEmail(), username).isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.CONFLICT, "The user is already taken!");
        }

        User updatedUser = userService.editUser(user.getId(), username, firstName, lastName, settings);
        if (updatedUser == null) {
            return requestService.executeApiResponse(HttpStatus.EXPECTATION_FAILED, "Unable to update the user!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The changes have been applied successfully!", updatedUser);
    }

    @Operation(summary = "Deletes the authorized user")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "200", description = "The user has been deleted successfully!")
    })
    @DeleteMapping
    public ResponseEntity<?> deleteAccount() {
        User user = getAuthorizedUser().get();
        userService.deleteUser(user);

        return requestService.executeApiResponse(HttpStatus.OK, "The user has been deleted successfully!");
    }

    @Operation(summary = "Gets the settings of the authorized user")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "200", description = "The settings have been retrieved successfully!")
    })
    @GetMapping("/settings")
    public ResponseEntity<?> settings() {
        User user = getAuthorizedUser().get();
        return requestService.executeEntityResponse(HttpStatus.OK, "The settings have been retrieved successfully!", user.getSettings());
    }
}