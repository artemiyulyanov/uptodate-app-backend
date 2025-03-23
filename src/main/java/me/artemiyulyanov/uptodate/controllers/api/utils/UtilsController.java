package me.artemiyulyanov.uptodate.controllers.api.utils;

import me.artemiyulyanov.uptodate.controllers.api.utils.responses.Timezone;
import me.artemiyulyanov.uptodate.controllers.api.utils.responses.TimezonesResponse;
import me.artemiyulyanov.uptodate.web.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/utils")
public class UtilsController {
    @Autowired
    private RequestService requestService;

    @GetMapping("/timezones")
    public ResponseEntity<?> getTimezones(@RequestParam(required = false, defaultValue = "") String query) {
        List<Timezone> timezones = ZoneId.getAvailableZoneIds().stream()
                .filter(zone -> zone.contains("/") && !zone.startsWith("Etc") && !zone.toLowerCase().contains("systemv"))
                .filter(zone -> zone.contains(query))
                .map(zone -> Timezone.builder()
                        .continent(zone.split("/")[0])
                        .fullName(zone)
                        .build())
                .collect(Collectors.toCollection(ArrayList::new));

        return requestService.executeCustomResponse(
                TimezonesResponse.builder()
                        .response(timezones)
                        .message("The timezones have been retrieved successfully!")
                        .status(200)
                        .build()
        );
    }
}
