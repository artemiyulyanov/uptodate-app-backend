package me.artemiyulyanov.uptodate.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Component
public class MailCache {
    public static final Duration MAIL_CODE_EXPIRATION = Duration.ofMinutes(15);

    @Autowired
    private RedisTemplate<String, MailConfirmationMessage> mailConfirmationMessages;

    public MailConfirmationMessage getMailConfirmationMessages(String id) {
        return mailConfirmationMessages.opsForValue().get(id);
    }

    public void addMailConfirmationMessage(MailConfirmationMessage confirmationCode) {
        mailConfirmationMessages.opsForValue().set(confirmationCode.getId(), confirmationCode, MAIL_CODE_EXPIRATION);
    }

    public void deleteMailConfirmationMessage(String id) {
        mailConfirmationMessages.delete(id);
    }

    public boolean hasMailConfirmationMessage(String id) {
        return Boolean.TRUE.equals(mailConfirmationMessages.hasKey(id));
    }

//    public boolean hasMailConfirmationMessage(String email, MailConfirmationMessage.MailScope scope) {
//        return hasConfirmationCodesFor(email) && Objects.requireNonNull(getMailConfirmationCode(email)).getScope().equals(scope);
//    }
}