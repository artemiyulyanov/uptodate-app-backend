package me.artemiyulyanov.uptodate.mail;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Transactional
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailCache mailCache;

    public MailConfirmationMessage getMailConfirmationMessage(String id) {
        return mailCache.getMailConfirmationMessages(id);
    }

    public boolean hasMailConfirmationMessage(String id) {
        return mailCache.hasMailConfirmationMessage(id);
    }

    public boolean performConfirmationFor(String id) {
        if (mailCache.hasMailConfirmationMessage(id)) {
            mailCache.deleteMailConfirmationMessage(id);
            return true;
        }

        return false;
    }
}