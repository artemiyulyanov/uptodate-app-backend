package me.artemiyulyanov.uptodate.controllers.api.account;

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
            @RequestParam String email
    ) {
        if (!userService.existsByEmail(email)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The user is not found by such email!");
        }

        MailConfirmationMessage mailConfirmationMessage = mailSenderFactory.createSender(MailConfirmationMessage.MailScope.PASSWORD_CHANGE)
                .send(email, Collections.emptyList());
        return requestService.executeApiResponse(HttpStatus.OK, "The confirmation link has been sent to your email address!");
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPassword(@RequestBody ConfirmPasswordRequest request) {
        if (!mailService.hasMailConfirmationMessage(request.getId())) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The confirmation message is not found!");
        }

        MailConfirmationMessage mailConfirmationMessage = mailService.getMailConfirmationMessage(request.getId());
        if (!mailConfirmationMessage.getScope().equals(MailConfirmationMessage.MailScope.PASSWORD_CHANGE)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The confirmation message id is invalid!");
        }

        if (!request.getPassword().equals(request.getRepeatedPassword())) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The passwords do not match!");
        }

        mailService.performConfirmationFor(request.getId());

        User updatedUser = userService.updatePassword(mailConfirmationMessage.getEmail(), request.getPassword());
        return requestService.executeEntityResponse(HttpStatus.OK, "The password has been updated successfully!", updatedUser);
    }
}