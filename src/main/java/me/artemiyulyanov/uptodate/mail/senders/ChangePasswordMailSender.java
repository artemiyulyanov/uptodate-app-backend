package me.artemiyulyanov.uptodate.mail.senders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.artemiyulyanov.uptodate.mail.MailCache;
import me.artemiyulyanov.uptodate.mail.MailConfirmationCode;
import me.artemiyulyanov.uptodate.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Component
public class ChangePasswordMailSender implements MailSender {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailService mailService;

    @Autowired
    private MailCache mailCache;

    @Override
    public MailConfirmationCode send(String email, List<MailConfirmationCode.Credential> credentials) {
        MailConfirmationCode confirmationCode = MailConfirmationCode.builder()
                .email(email)
                .code(Integer.toString(mailService.generateRandomCode()))
                .credentials(credentials)
                .scope(MailConfirmationCode.MailScope.PASSWORD_CHANGE)
                .build();
        mailCache.setMailConfirmationCode(email, confirmationCode);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Confirmation code");
        message.setText(String.format("Hi! Your confirmation code is: %s. Enter it to change your password", confirmationCode.getCode()));
        javaMailSender.send(message);

        return confirmationCode;
    }
}