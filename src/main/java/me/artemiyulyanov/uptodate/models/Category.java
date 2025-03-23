package me.artemiyulyanov.uptodate.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import me.artemiyulyanov.uptodate.models.converters.TranslativeStringConverter;
import me.artemiyulyanov.uptodate.models.text.TranslativeString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = TranslativeStringConverter.class)
    private TranslativeString parent;

    @Convert(converter = TranslativeStringConverter.class)
    @Column(unique = true)
    private TranslativeString name;

    @JsonIgnore
    @ManyToMany(mappedBy = "categories")
    @Builder.Default
    private Set<Article> articles = new HashSet<>();

    public int getCount() {
        return articles.size();
    }

    public static Category of(String englishParent, String russianParent, String englishName, String russianName) {
        return Category.builder()
                .parent(TranslativeString.builder()
                        .english(englishParent)
                        .russian(russianParent)
                        .build())
                .name(TranslativeString.builder()
                        .english(englishName)
                        .russian(russianName)
                        .build())
                .build();
    }
}