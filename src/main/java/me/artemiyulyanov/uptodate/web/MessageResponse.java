package me.artemiyulyanov.uptodate.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Component
public class MessageResponse extends ServerResponse<Void> {
    @JsonIgnore
    @Override
    public Void getResponse() {
        return super.getResponse();
    }
}