package me.artemiyulyanov.uptodate.mail.senders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.artemiyulyanov.uptodate.mail.MailCache;
import me.artemiyulyanov.uptodate.mail.MailConfirmationMessage;
import me.artemiyulyanov.uptodate.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Component
public class RegistrationMailSender implements MailSender {
    private JavaMailSender javaMailSender;
    private MailService mailService;
    private MailCache mailCache;
    private String registrationMailMessageUrl;

    @Override
    public MailConfirmationMessage send(String email, List<MailConfirmationMessage.Credential> credentials) {
        MailConfirmationMessage mailConfirmationMessage = MailConfirmationMessage.builder()
                .id(MailConfirmationMessage.generateConfirmationMessageId())
                .email(email)
                .credentials(credentials)
//                .sentAt(LocalDateTime.now())
                .scope(MailConfirmationMessage.MailScope.REGISTRATION)
                .build();
        mailCache.addMailConfirmationMessage(mailConfirmationMessage);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Confirmation link");
        message.setText(String.format("Hi! Follow this confirmation link to finish your registration: %s", registrationMailMessageUrl + mailConfirmationMessage.getId()));
        javaMailSender.send(message);

        return mailConfirmationMessage;
    }
}