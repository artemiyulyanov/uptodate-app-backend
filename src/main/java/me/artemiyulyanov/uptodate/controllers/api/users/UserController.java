package me.artemiyulyanov.uptodate.controllers.api.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.artemiyulyanov.uptodate.controllers.AuthenticatedController;
import me.artemiyulyanov.uptodate.models.Article;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.services.UserService;
import me.artemiyulyanov.uptodate.web.RequestService;
import me.artemiyulyanov.uptodate.web.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints to interact with users")
public class UserController extends AuthenticatedController {
    @Autowired
    private UserService userService;

    @Autowired
    private RequestService requestService;

    @Operation(summary = "Gets users by their IDs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The users have been retrieved successfully!")
    })
    @GetMapping
    public ResponseEntity<?> getUsersByIds(
            @Parameter(name = "The IDs of users to be found")
            @RequestParam(defaultValue = "", required = false)
            List<Long> ids
    ) {
        List<User> users = userService.getAllUsers(ids);
        return requestService.executeEntityResponse(HttpStatus.OK, "The users have been retrieved successfully!", users);
    }

    @Operation(summary = "Gets an user")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "The user is not found!"),
            @ApiResponse(responseCode = "200", description = "The user has been retrieved successfully!")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @Parameter(name = "The ID of user to be found")
            @PathVariable
            Long id
    ) {
        Optional<User> wrappedUser = userService.getUserById(id);

        if (wrappedUser.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "User is undefined!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The user has been retrieved successfully!", wrappedUser.get());
    }
}