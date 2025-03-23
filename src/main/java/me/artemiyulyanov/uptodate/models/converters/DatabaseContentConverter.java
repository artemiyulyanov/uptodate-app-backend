package me.artemiyulyanov.uptodate.models.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import me.artemiyulyanov.uptodate.models.ContentBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Converter(autoApply = true)
@Component
public class DatabaseContentConverter implements AttributeConverter<List<ContentBlock>, String> {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(List<ContentBlock> contentBlocks) {
        try {
            return objectMapper.writeValueAsString(contentBlocks);
        } catch (Exception e) {
            return "[]";
        }
    }

    @Override
    public List<ContentBlock> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}