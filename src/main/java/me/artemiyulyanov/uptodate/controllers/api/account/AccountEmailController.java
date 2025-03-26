package me.artemiyulyanov.uptodate.controllers.api.account;

import me.artemiyulyanov.uptodate.controllers.AuthenticatedController;
import me.artemiyulyanov.uptodate.controllers.api.account.requests.ConfirmEmailRequest;
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
public class AccountEmailController extends AuthenticatedController {
    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private MailSenderFactory mailSenderFactory;

    @PatchMapping
    public ResponseEntity<?> editEmail(@RequestParam String email) {
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

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmEmail(@RequestBody ConfirmEmailRequest request) {
        User user = getAuthorizedUser().get();

        if (!mailService.hasMailConfirmationMessage(request.getId())) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The confirmation message is not found!");
        }

        MailConfirmationMessage mailConfirmationMessage = mailService.getMailConfirmationMessage(request.getId());
        if (!mailConfirmationMessage.getScope().equals(MailConfirmationMessage.MailScope.EMAIL_CHANGE)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The confirmation message id is invalid!");
        }

        Long userToChangeEmailId = mailConfirmationMessage.getCredential("userId").getValue(Long.class);

        if (!userToChangeEmailId.equals(user.getId())) {
            return requestService.executeApiResponse(HttpStatus.FORBIDDEN, "You are not allowed to confirm this email!");
        }

        mailService.performConfirmationFor(request.getId());

        User updatedUser = userService.updateEmail(user, mailConfirmationMessage.getEmail());
        return requestService.executeEntityResponse(HttpStatus.OK, "The email has been updated successfully!", updatedUser);
    }
}