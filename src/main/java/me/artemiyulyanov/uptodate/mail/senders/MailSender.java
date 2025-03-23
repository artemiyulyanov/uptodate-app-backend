package me.artemiyulyanov.uptodate.mail.senders;

import lombok.*;
import me.artemiyulyanov.uptodate.mail.MailConfirmationCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MailSender {
    MailConfirmationCode send(String email, List<MailConfirmationCode.Credential> credentials);
}