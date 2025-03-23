package me.artemiyulyanov.uptodate.controllers.api.files.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import me.artemiyulyanov.uptodate.web.ServerResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Component
public class FileUploadResponse extends ServerResponse<Map<String, Object>> {
    @JsonIgnore
    private String path;

    @Override
    public Map<String, Object> getResponse() {
        return Map.of("path", path);
    }
}