package me.artemiyulyanov.uptodate.mail;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MailConfirmationCode {
    private String email, code;
    private List<Credential> credentials;
    private MailScope scope;

    public Credential getCredential(String key) {
        return credentials.stream().filter(credential -> credential.getKey().equals(key)).findAny().get();
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