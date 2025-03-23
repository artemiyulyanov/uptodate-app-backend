package me.artemiyulyanov.uptodate.controllers.api.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
public class UserController extends AuthenticatedController {
    @Autowired
    private UserService userService;

    @Autowired
    private RequestService requestService;

    @GetMapping
    public ResponseEntity<?> getUsersByIds(@RequestParam(defaultValue = "", required = false) List<Long> ids) {
        List<User> users = userService.findAllById(ids);
        return requestService.executeEntityResponse(HttpStatus.OK, "The users have been retrieved successfully!", users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> wrappedUser = userService.findById(id);

        if (wrappedUser.isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "User is undefined!");
        }

        return requestService.executeEntityResponse(HttpStatus.OK, "The user has been retrieved successfully!", wrappedUser.get());
    }
}