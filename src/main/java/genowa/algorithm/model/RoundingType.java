package genowa.algorithm.model;

/**
 * Rounding types used in algorithm steps.
 * 
 * Maps to legacy WARP_ROU_TYPE_CD.
 */
public enum RoundingType {
    
    /**
     * No rounding - keep full precision.
     * Legacy code: '0'
     */
    NONE("0", "No rounding"),
    
    /**
     * Decimal rounding - round to specified decimal places.
     * Uses WARP_ROU_VALUE_AMT for precision.
     * Legacy code: 'D'
     */
    DECIMAL("D", "Round to decimal places"),
    
    /**
     * Whole dollar rounding - round to nearest whole number.
     * Legacy code: 'W'
     */
    WHOLE("W", "Round to whole dollar");
    
    private final String code;
    private final String description;
    
    RoundingType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Get RoundingType from its code.
     */
    public static RoundingType fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return NONE;
        }
        
        for (RoundingType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown RoundingType code: " + code);
    }
    
    /**
     * Convert from legacy WARP_ROU_TYPE_CD.
     */
    public static RoundingType fromLegacy(String legacyCode) {
        if (legacyCode == null || legacyCode.trim().isEmpty() || "0".equals(legacyCode.trim())) {
            return NONE;
        }
        
        return switch (legacyCode.trim().toUpperCase()) {
            case "D" -> DECIMAL;
            case "W" -> WHOLE;
            default -> throw new IllegalArgumentException(
                "Unknown legacy rounding code: " + legacyCode);
        };
    }
}
