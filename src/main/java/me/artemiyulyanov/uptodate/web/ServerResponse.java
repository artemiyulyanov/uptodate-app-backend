package me.artemiyulyanov.uptodate.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Component
public class ServerResponse<T> {
    private T response;
    private String message;
    private int status;

    @Builder.Default
    private Long timestamp = System.currentTimeMillis();
}