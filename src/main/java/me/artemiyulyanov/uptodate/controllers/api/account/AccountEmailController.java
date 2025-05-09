package me.artemiyulyanov.uptodate.controllers.api.account;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.artemiyulyanov.uptodate.controllers.AuthenticatedController;
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

import java.util.List;

@RestController
@RequestMapping("/api/account/email")
@Tag(name = "Account Email", description = "Endpoints to interact with account's email")
public class AccountEmailController extends AuthenticatedController {
    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private MailSenderFactory mailSenderFactory;

    @Operation(summary = "Changes email")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "409", description = "The email is already taken!"),
            @ApiResponse(responseCode = "200", description = "The confirmation link has been sent to your email!")
    })
    @PatchMapping
    public ResponseEntity<?> editEmail(
            @Parameter(name = "A new email")
            @RequestParam
            String email
    ) {
        User user = getAuthorizedUser().get();

        if (!userService.getConflictedColumnsWhileEditing(user, email, user.getUsername()).isEmpty()) {
            return requestService.executeApiResponse(HttpStatus.CONFLICT, "The email is already taken!");
        }

        MailConfirmationMessage mailConfirmationMessage = mailSenderFactory.createSender(MailConfirmationMessage.MailScope.EMAIL_CHANGE)
                .send(email, List.of(
                        MailConfirmationMessage.Credential
                                .builder()
                                .key("userId")
                                .value(user.getId())
                                .build()
                ));
        return requestService.executeApiResponse(HttpStatus.OK, "The confirmation link has been sent to your email address!");
    }

    @Operation(summary = "Confirms email")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "The confirmation link is not found!"),
            @ApiResponse(responseCode = "401", description = "The user is unauthorized!"),
            @ApiResponse(responseCode = "403", description = "The user is not allowed to confirm this email"),
            @ApiResponse(responseCode = "200", description = "The email has been confirmed successfully!")
    })
    @PostMapping("/confirm/{id}")
    public ResponseEntity<?> confirmEmail(
            @Parameter(name = "The ID of link")
            @PathVariable
            String id
    ) {
        User user = getAuthorizedUser().get();

        if (!mailService.hasMailConfirmationMessage(id)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The confirmation message is not found!");
        }

        MailConfirmationMessage mailConfirmationMessage = mailService.getMailConfirmationMessage(id);
        if (!mailConfirmationMessage.getScope().equals(MailConfirmationMessage.MailScope.EMAIL_CHANGE)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The confirmation message id is invalid!");
        }

        Long userToChangeEmailId = mailConfirmationMessage.getCredential("userId").getValue(Long.class);

        if (!userToChangeEmailId.equals(user.getId())) {
            return requestService.executeApiResponse(HttpStatus.FORBIDDEN, "You are not allowed to confirm this email!");
        }

        mailService.performConfirmationFor(id);

        User updatedUser = userService.updateEmail(user, mailConfirmationMessage.getEmail());
        return requestService.executeEntityResponse(HttpStatus.OK, "The email has been updated successfully!", updatedUser);
    }
}