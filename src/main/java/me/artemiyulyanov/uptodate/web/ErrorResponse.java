package me.artemiyulyanov.uptodate.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Component
public class ErrorResponse extends ServerResponse<Void> {
    private String error;

    @JsonIgnore
    @Override
    public Void getResponse() {
        return super.getResponse();
    }

    @JsonIgnore
    @Override
    public String getMessage() {
        return super.getMessage();
    }
}