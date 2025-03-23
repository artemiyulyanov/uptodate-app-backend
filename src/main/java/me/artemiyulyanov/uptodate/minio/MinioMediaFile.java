package me.artemiyulyanov.uptodate.minio;

import lombok.*;
import org.springframework.http.MediaType;

import javax.print.attribute.standard.Media;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MinioMediaFile {
    public static final Map<String, MediaType> AVAILABLE_TYPES = Map.of(
            ".png", MediaType.IMAGE_PNG,
            ".jpg", MediaType.IMAGE_JPEG,
            ".jpeg", MediaType.IMAGE_JPEG,
            ".gif", MediaType.IMAGE_GIF
    );

    private String objectKey;
    private InputStream inputStream;

    public MediaType getMediaType() {
        return getMediaType(objectKey);
    }

    public static boolean isAvailable(String path) {
        return AVAILABLE_TYPES
                .keySet()
                .stream()
                .anyMatch(path::endsWith);
    }

    public static MediaType getMediaType(String path) {
        if (!isAvailable(path)) return MediaType.ALL;

        String format = AVAILABLE_TYPES
                .keySet()
                .stream()
                .filter(path::endsWith)
                .findAny()
                .get();

        return AVAILABLE_TYPES.get(format);
    }
}