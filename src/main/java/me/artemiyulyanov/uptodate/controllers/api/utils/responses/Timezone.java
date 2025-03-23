package me.artemiyulyanov.uptodate.controllers.api.utils.responses;

import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Component
public class Timezone {
    private String continent, fullName;
}