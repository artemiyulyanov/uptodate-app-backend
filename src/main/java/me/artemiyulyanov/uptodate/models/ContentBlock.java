package me.artemiyulyanov.uptodate.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentBlock {
    private ContentBlockType type;

    private String text;

    public enum ContentBlockType {
        HEADING, TEXT, LIST, IMAGE;
    }
}
