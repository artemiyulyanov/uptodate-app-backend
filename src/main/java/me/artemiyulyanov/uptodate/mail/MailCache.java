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
    private RedisTemplate<String, MailConfirmationCode> confirmationCodes;

    public MailConfirmationCode getMailConfirmationCode(String email) {
        return confirmationCodes.opsForValue().get(email);
    }

    public void setMailConfirmationCode(String email, MailConfirmationCode confirmationCode) {
        confirmationCodes.opsForValue().set(email, confirmationCode, MAIL_CODE_EXPIRATION);
    }

    public void deleteConfirmationCode(String email) {
        confirmationCodes.delete(email);
    }

    public boolean hasConfirmationCodesFor(String email) {
        return Boolean.TRUE.equals(confirmationCodes.hasKey(email));
    }

    public boolean hasConfirmationCode(String email, MailConfirmationCode.MailScope scope) {
        return hasConfirmationCodesFor(email) && Objects.requireNonNull(getMailConfirmationCode(email)).getScope().equals(scope);
    }
}