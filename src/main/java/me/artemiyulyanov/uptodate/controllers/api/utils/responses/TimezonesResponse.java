package me.artemiyulyanov.uptodate.controllers.api.utils.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import me.artemiyulyanov.uptodate.web.ServerResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@Component
public class TimezonesResponse extends ServerResponse<List<Timezone>> {
}