package me.artemiyulyanov.uptodate.controllers.api.account;

import me.artemiyulyanov.uptodate.controllers.AuthenticatedController;
import me.artemiyulyanov.uptodate.mail.MailConfirmationCode;
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

        MailConfirmationCode mailConfirmationCode = mailSenderFactory.createSender(MailConfirmationCode.MailScope.EMAIL_CHANGE)
                .send(email, List.of(
                        MailConfirmationCode.Credential
                                .builder()
                                .key("userId")
                                .value(user.getId())
                                .build()
                ));
        return requestService.executeApiResponse(HttpStatus.OK, "The code has been sent to your email address!");
    }

    @PostMapping
    public ResponseEntity<?> confirmEmail(
            @RequestParam String email,
            @RequestParam String code) {
        User user = getAuthorizedUser().get();

        if (!mailService.validateCode(email, code, MailConfirmationCode.MailScope.EMAIL_CHANGE)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The code is invalid!");
        }

        MailConfirmationCode mailConfirmationCode = mailService.getConfirmationCode(email);
        Long userToChangeEmailId = mailConfirmationCode.getCredential("userId").getValue(Long.class);

        if (!userToChangeEmailId.equals(user.getId())) {
            return requestService.executeApiResponse(HttpStatus.FORBIDDEN, "You are not allowed to confirm this email!");
        }

        mailService.enterCode(email, code, MailConfirmationCode.MailScope.EMAIL_CHANGE);

        User updatedUser = userService.updateEmail(user, email);
        return requestService.executeEntityResponse(HttpStatus.OK, "The email has been updated successfully!", updatedUser);
    }
}