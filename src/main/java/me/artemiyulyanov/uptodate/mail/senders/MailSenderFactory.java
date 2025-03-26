package me.artemiyulyanov.uptodate.mail.senders;

import me.artemiyulyanov.uptodate.mail.MailCache;
import me.artemiyulyanov.uptodate.mail.MailConfirmationMessage;
import me.artemiyulyanov.uptodate.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    @Qualifier("registrationMailMessageUrl")
    private String registrationMailMessageUrl;

    @Autowired
    @Qualifier("changeEmailMailMessageUrl")
    private String changeEmailMailMessageUrl;

    @Autowired
    @Qualifier("changePasswordMailMessageUrl")
    private String changePasswordMailMessageUrl;

    public MailSender createSender(MailConfirmationMessage.MailScope scope) {
        if (scope == MailConfirmationMessage.MailScope.REGISTRATION) {
            return RegistrationMailSender.builder()
                    .javaMailSender(javaMailSender)
                    .mailService(mailService)
                    .mailCache(mailCache)
                    .registrationMailMessageUrl(registrationMailMessageUrl)
                    .build();
        }

        if (scope == MailConfirmationMessage.MailScope.EMAIL_CHANGE) {
            return ChangeEmailMailSender.builder()
                    .javaMailSender(javaMailSender)
                    .mailService(mailService)
                    .mailCache(mailCache)
                    .changeEmailMailMessageUrl(changeEmailMailMessageUrl)
                    .build();
        }

        if (scope == MailConfirmationMessage.MailScope.PASSWORD_CHANGE) {
            return ChangePasswordMailSender.builder()
                    .javaMailSender(javaMailSender)
                    .mailService(mailService)
                    .mailCache(mailCache)
                    .changePasswordMailMessageUrl(changePasswordMailMessageUrl)
                    .build();
        }

        return null;
    }
}