package me.artemiyulyanov.uptodate.models.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import me.artemiyulyanov.uptodate.models.text.TranslativeString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class TranslativeStringConverter implements AttributeConverter<TranslativeString, String> {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(TranslativeString translativeString) {
        if (translativeString == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(translativeString);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting TranslativeString to JSON", e);
        }
    }

    @Override
    public TranslativeString convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, TranslativeString.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to TranslativeString", e);
        }
    }
}
