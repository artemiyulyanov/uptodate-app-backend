package me.artemiyulyanov.uptodate.mail.senders;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.artemiyulyanov.uptodate.mail.MailCache;
import me.artemiyulyanov.uptodate.mail.MailConfirmationCode;
import me.artemiyulyanov.uptodate.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailSenderFactory {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailService mailService;

    @Autowired
    private MailCache mailCache;

    public MailSender createSender(MailConfirmationCode.MailScope scope) {
        if (scope == MailConfirmationCode.MailScope.REGISTRATION) {
            return RegistrationMailSender.builder()
                    .javaMailSender(javaMailSender)
                    .mailService(mailService)
                    .mailCache(mailCache)
                    .build();
        }

        if (scope == MailConfirmationCode.MailScope.EMAIL_CHANGE) {
            return ChangeEmailMailSender.builder()
                    .javaMailSender(javaMailSender)
                    .mailService(mailService)
                    .mailCache(mailCache)
                    .build();
        }

        if (scope == MailConfirmationCode.MailScope.PASSWORD_CHANGE) {
            return ChangePasswordMailSender.builder()
                    .javaMailSender(javaMailSender)
                    .mailService(mailService)
                    .mailCache(mailCache)
                    .build();
        }

        return null;
    }
}