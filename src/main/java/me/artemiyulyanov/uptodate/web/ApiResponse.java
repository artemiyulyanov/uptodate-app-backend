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
@SuperBuilder
@Component
public class ApiResponse extends ServerResponse<Void> {
    @JsonIgnore
    @Override
    public Void getResponse() {
        return super.getResponse();
    }
}