package me.artemiyulyanov.uptodate.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Component
public class CustomResponse extends ServerResponse<Map<String, Object>> {
    private String message;
}