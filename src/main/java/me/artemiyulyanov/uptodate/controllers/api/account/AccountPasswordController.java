package me.artemiyulyanov.uptodate.controllers.api.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.artemiyulyanov.uptodate.controllers.AuthenticatedController;
import me.artemiyulyanov.uptodate.controllers.api.account.requests.ConfirmPasswordRequest;
import me.artemiyulyanov.uptodate.mail.MailConfirmationMessage;
import me.artemiyulyanov.uptodate.mail.MailService;
import me.artemiyulyanov.uptodate.mail.senders.MailSenderFactory;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.services.UserService;
import me.artemiyulyanov.uptodate.web.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/account/password")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Account Password", description = "Endpoints to interact with account's password")
public class AccountPasswordController extends AuthenticatedController {
    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private MailSenderFactory mailSenderFactory;

    @Operation(summary = "Resets password", description = "Sends a link to reset password")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "The user is not found!"),
            @ApiResponse(responseCode = "200", description = "The link has been sent to your email address!")
    })
    @PostMapping
    public ResponseEntity<?> editPassword(
            @Parameter(name = "The email of user", description = "Requires because the user may be unauthorized")
            @RequestParam
            String email
    ) {
        if (!userService.existsByEmail(email)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The user is not found by such email!");
        }

        MailConfirmationMessage mailConfirmationMessage = mailSenderFactory.createSender(MailConfirmationMessage.MailScope.PASSWORD_CHANGE)
                .send(email, Collections.emptyList());
        return requestService.executeApiResponse(HttpStatus.OK, "The link has been sent to your email address!");
    }

    @Operation(summary = "Confirms and sets up a new password")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Error to process the data"),
            @ApiResponse(responseCode = "200", description = "The password has been changed successfully!")
    })
    @PatchMapping("/confirm/{id}")
    public ResponseEntity<?> confirmPassword(
            @Parameter(name = "The ID of link")
            @RequestParam
            String id,
            @RequestBody
            ConfirmPasswordRequest request
    ) {
        if (!mailService.hasMailConfirmationMessage(id)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The confirmation message is not found!");
        }

        MailConfirmationMessage mailConfirmationMessage = mailService.getMailConfirmationMessage(id);
        if (!mailConfirmationMessage.getScope().equals(MailConfirmationMessage.MailScope.PASSWORD_CHANGE)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The confirmation message id is invalid!");
        }

        if (!request.getPassword().equals(request.getRepeatedPassword())) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The passwords do not match!");
        }

        mailService.performConfirmationFor(id);

        User updatedUser = userService.updatePassword(mailConfirmationMessage.getEmail(), request.getPassword());
        return requestService.executeEntityResponse(HttpStatus.OK, "The password has been updated successfully!", updatedUser);
    }
}