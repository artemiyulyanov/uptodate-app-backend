package me.artemiyulyanov.uptodate.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

@Service
public class RequestService {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        return objectMapper;
    }

    public String mapToJson(Map<String, Object> params) {
        try {
            return objectMapper().writeValueAsString(params);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Now all the server responses whether errors or messages are supposed to be executed via executeApiResponse() method **/
    @Deprecated
    public ResponseEntity<ServerResponse> executeErrorResponse(HttpStatus status, String error) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse
                        .builder()
                        .status(status.value())
                        .error(error)
                        .build()
                );
    }

    /** Now all the server responses whether errors or messages are supposed to be executed via executeApiResponse() method **/
    @Deprecated
    public ResponseEntity<ServerResponse> executeMessageResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(MessageResponse
                        .builder()
                        .status(status.value())
                        .message(message)
                        .build()
                );
    }

    public ResponseEntity<?> executeApiResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse
                        .builder()
                        .status(status.value())
                        .message(message)
                        .build()
                );
    }

    public ResponseEntity<?> executeEntityResponse(HttpStatus status, String message, Object entity) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ServerResponse
                        .builder()
                        .status(status.value())
                        .message(message)
                        .response(entity)
                        .build()
                );
    }

    public <T> ResponseEntity<?> executePaginatedEntityResponse(HttpStatus status, Page<T> entity) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(PaginatedResponse
                        .builder()
                        .totalPages(entity.getTotalPages())
                        .page(entity.getNumber())
                        .size(entity.getSize())
                        .totalElements(entity.getTotalElements())
                        .last(entity.isLast())
                        .status(status.value())
                        .response(entity.getContent())
                        .build()
                );
    }

    /** Now custom templates are supposed to be executed via executeCustom() method and require ServerResponse<T> examples of class **/
    @Deprecated
    public ResponseEntity<?> executeTemplateResponse(HttpStatus status, String message, Map<String, Object> response) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ServerResponse
                        .builder()
                        .status(status.value())
                        .message(message)
                        .response(response)
                        .build()
                );
    }

    public ResponseEntity<?> executeCustomResponse(ServerResponse<?> response) {
        return ResponseEntity.status(response.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    public ResponseEntity<Void> executeRedirection(String path, Map<String, String> queryParams) {
        String redirectUrl = queryParams.entrySet()
                .stream()
                .reduce(
                        UriComponentsBuilder.fromPath(path),
                        (accumulator, param) -> accumulator.queryParam(param.getKey(), param.getValue()),
                        (acc1, acc2) -> acc1
                ).toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", redirectUrl);
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }

    public ResponseEntity<byte[]> executeMediaResponse(HttpStatus status, MediaType mediaType, byte[] image) {
        return ResponseEntity.status(status)
                .contentType(mediaType)
                .contentLength(image.length)
                .body(image);
    }
}