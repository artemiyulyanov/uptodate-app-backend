package me.artemiyulyanov.uptodate.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import me.artemiyulyanov.uptodate.models.converters.DatabaseContentConverter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "faq_items")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FaqItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    @JsonIgnoreProperties({"items"})
    private FaqSection section;

    @Builder.Default
    @Convert(converter = DatabaseContentConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<ContentBlock> content = new ArrayList<>();
}