package me.artemiyulyanov.uptodate.controllers.api.account;

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
public class AccountIconController extends AuthenticatedController {
    @Autowired
    private UserService userService;

    @Autowired
    private RequestService requestService;

    @PutMapping
    public ResponseEntity<?> uploadIcon(@RequestParam(value = "icon") MultipartFile icon) {
        User updatedUser = userService.uploadIcon(getAuthorizedUser().get().getId(), icon);
        return requestService.executeEntityResponse(HttpStatus.OK, "The icon has been updated successfully!", updatedUser);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteIcon() {
        User updatedUser = userService.deleteIcon(getAuthorizedUser().get().getId());
        return requestService.executeEntityResponse(HttpStatus.OK, "The icon has been deleted successfully!", updatedUser);
    }
}