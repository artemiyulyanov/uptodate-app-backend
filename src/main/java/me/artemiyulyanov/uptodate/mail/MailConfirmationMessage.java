package me.artemiyulyanov.uptodate.mail;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MailConfirmationMessage {
    private String id, email;
    private List<Credential> credentials;
    private MailScope scope;

//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
//    private LocalDateTime sentAt;

    public Credential getCredential(String key) {
        return credentials.stream().filter(credential -> credential.getKey().equals(key)).findAny().get();
    }

    public static String generateConfirmationMessageId() {
        return UUID.randomUUID().toString();
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Credential {
        @Getter
        @Setter
        private String key;

        @Getter
        @Setter
        private Object value;

        public <T> T getValue(Class<T> classOfValue) {
            return classOfValue.cast(value);
        }
    }

    public enum MailScope {
        REGISTRATION, EMAIL_CHANGE, PASSWORD_CHANGE;
    }
}