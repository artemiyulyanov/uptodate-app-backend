package me.artemiyulyanov.uptodate.models.text;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Builder
@AllArgsConstructor
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