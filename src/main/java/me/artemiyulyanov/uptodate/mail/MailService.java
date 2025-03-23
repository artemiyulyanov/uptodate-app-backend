package me.artemiyulyanov.uptodate.mail;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailCache mailCache;

    public MailConfirmationCode getConfirmationCode(String email) {
        return mailCache.getMailConfirmationCode(email);
    }

    public boolean enterCode(String email, String code, MailConfirmationCode.MailScope scope) {
        if (validateCode(email, code, scope)) {
            mailCache.deleteConfirmationCode(email);
            return true;
        }

        return false;
    }

    public boolean validateCode(String email, String code, MailConfirmationCode.MailScope scope) {
        return mailCache.hasConfirmationCode(email, scope) && Objects.requireNonNull(mailCache.getMailConfirmationCode(email)).getCode().equals(code);
    }

    public boolean isCodeSent(String email, MailConfirmationCode.MailScope scope) {
        return mailCache.hasConfirmationCode(email, scope);
    }

    public int generateRandomCode() {
        return (int) (Math.random() * 899999) + 100000;
    }
}