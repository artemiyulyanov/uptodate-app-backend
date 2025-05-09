package me.artemiyulyanov.uptodate.controllers.api.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.artemiyulyanov.uptodate.controllers.AuthenticatedController;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.services.UserService;
import me.artemiyulyanov.uptodate.web.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/account/icon")
@Tag(name = "Account Icon", description = "Endpoints to interact with account's icon")
public class AccountIconController extends AuthenticatedController {
    @Autowired
    private UserService userService;

    @Autowired
    private RequestService requestService;

    @Operation(summary = "Uploads a new icon")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "200", description = "The icon has been uploaded successfully!")
    })
    @PatchMapping
    public ResponseEntity<?> uploadIcon(
            @Parameter(name = "A new icon to upload")
            @RequestParam(value = "icon")
            MultipartFile icon
    ) {
        User updatedUser = userService.uploadIcon(getAuthorizedUser().get().getId(), icon);
        return requestService.executeEntityResponse(HttpStatus.OK, "The icon has been updated successfully!", updatedUser);
    }

    @Operation(summary = "Deletes icon")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "200", description = "The icon has been deleted successfully!")
    })
    @DeleteMapping
    public ResponseEntity<?> deleteIcon() {
        User updatedUser = userService.deleteIcon(getAuthorizedUser().get().getId());
        return requestService.executeEntityResponse(HttpStatus.OK, "The icon has been deleted successfully!", updatedUser);
    }
}