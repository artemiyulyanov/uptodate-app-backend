package me.artemiyulyanov.uptodate.models.text;

import jakarta.persistence.Embeddable;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranslativeString {
    private String english, russian;

    @Override
    public String toString() {
        return "TranslativeString{" +
                "english='" + english + '\'' +
                ", russian='" + russian + '\'' +
                '}';
    }
}