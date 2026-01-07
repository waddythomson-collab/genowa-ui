package genowa.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for LevelType enum to database code.
 * Converts between LevelType enum and single-character database code.
 */
@Converter(autoApply = true)
public class LevelTypeConverter implements AttributeConverter<LevelType, String> {
    
    @Override
    public String convertToDatabaseColumn(LevelType levelType) {
        return levelType == null ? null : levelType.getCode();
    }
    
    @Override
    public LevelType convertToEntityAttribute(String code) {
        return code == null ? null : LevelType.fromCode(code);
    }
}
