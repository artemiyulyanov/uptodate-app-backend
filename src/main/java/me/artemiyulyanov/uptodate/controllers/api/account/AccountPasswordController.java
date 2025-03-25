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
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String repeatedPassword
    ) {
        if (!userService.existsByEmail(email)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The user is not found by such email!");
        }

        if (!password.equals(repeatedPassword)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The passwords do not match!");
        }

        MailConfirmationCode mailConfirmationCode = mailSenderFactory.createSender(MailConfirmationCode.MailScope.PASSWORD_CHANGE)
                .send(email, List.of(
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
            @RequestParam String email,
            @RequestParam String code
    ) {
        if (!mailService.validateCode(email, code, MailConfirmationCode.MailScope.PASSWORD_CHANGE)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The code is invalid!");
        }

        MailConfirmationCode mailConfirmationCode = mailService.getConfirmationCode(email);
        String password = mailConfirmationCode.getCredential("password").getValue(String.class);

        mailService.enterCode(email, code, MailConfirmationCode.MailScope.PASSWORD_CHANGE);

        User updatedUser = userService.updatePassword(email, password);
        return requestService.executeEntityResponse(HttpStatus.OK, "The password has been updated successfully!", updatedUser);
    }
}