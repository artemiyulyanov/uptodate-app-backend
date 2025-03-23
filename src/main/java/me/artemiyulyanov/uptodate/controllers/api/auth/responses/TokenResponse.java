package me.artemiyulyanov.uptodate.controllers.api.auth.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import me.artemiyulyanov.uptodate.web.ServerResponse;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Component
public class TokenResponse extends ServerResponse<Void> {
    private String access_token, refresh_token;

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