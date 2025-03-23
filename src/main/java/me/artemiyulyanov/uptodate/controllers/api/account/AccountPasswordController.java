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
@RequestMapping("/api/account/password")
public class AccountPasswordController extends AuthenticatedController {
    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private MailSenderFactory mailSenderFactory;

    @PatchMapping
    public ResponseEntity<?> editPassword(
            @RequestParam String password,
            @RequestParam String repeatedPassword
    ) {
        User user = getAuthorizedUser().get();

        if (!password.equals(repeatedPassword)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The passwords do not match!");
        }

        MailConfirmationCode mailConfirmationCode = mailSenderFactory.createSender(MailConfirmationCode.MailScope.PASSWORD_CHANGE)
                .send(user.getEmail(), List.of(
                        MailConfirmationCode.Credential
                                .builder()
                                .key("password")
                                .value(password)
                                .build()
                ));
        return requestService.executeApiResponse(HttpStatus.OK, "The code has been sent to your email address!");
    }

    @PostMapping
    public ResponseEntity<?> confirmPassword(
            @RequestParam String code
    ) {
        User user = getAuthorizedUser().get();

        if (!mailService.validateCode(user.getEmail(), code, MailConfirmationCode.MailScope.PASSWORD_CHANGE)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The code is invalid!");
        }

        MailConfirmationCode mailConfirmationCode = mailService.getConfirmationCode(user.getEmail());
        String password = mailConfirmationCode.getCredential("password").getValue(String.class);

        mailService.enterCode(user.getEmail(), code, MailConfirmationCode.MailScope.PASSWORD_CHANGE);

        User updatedUser = userService.updatePassword(user, password);
        return requestService.executeEntityResponse(HttpStatus.OK, "The password has been updated successfully!", updatedUser);
    }
}