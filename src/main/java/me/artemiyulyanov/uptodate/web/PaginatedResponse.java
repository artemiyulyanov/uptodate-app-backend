package me.artemiyulyanov.uptodate.web;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Component
public class PaginatedResponse<T> extends ServerResponse<T> {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}