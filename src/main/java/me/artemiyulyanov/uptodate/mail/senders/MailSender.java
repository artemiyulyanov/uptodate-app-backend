package me.artemiyulyanov.uptodate.mail.senders;

import me.artemiyulyanov.uptodate.mail.MailConfirmationMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MailSender {
    MailConfirmationMessage send(String email, List<MailConfirmationMessage.Credential> credentials);
}