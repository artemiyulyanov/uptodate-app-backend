package me.artemiyulyanov.uptodate.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users_settings")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSettings {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    @JsonIgnore
    private User user;

    private String language, timezone;

    public static UserSettings getDefaultSettings(User user) {
        return UserSettings.builder()
                .user(user)
                .language("en")
                .timezone("Europe/Moscow")
                .build();
    }
}