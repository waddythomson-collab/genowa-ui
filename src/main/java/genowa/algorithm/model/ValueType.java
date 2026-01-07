package genowa.algorithm.model;

/**
 * Defines the types of elements that can be used in algorithm steps.
 * 
 * Enhanced from legacy WARP_VALUE_TYPE_CD codes to provide explicit typing.
 * During migration, legacy '0' codes are resolved via JOINs to determine
 * the actual type (RATE_KEY, DATABASE, or BOOLEAN).
 * 
 * The BOOLEAN type replaces the need for a separate conditional indicator field.
 */
public enum ValueType
{
    /**
     * User-defined code that requires lookup/translation.
     * Legacy code: 'C'
     */
    CODE("C", "User-defined code requiring lookup"),
    
    /**
     * Literal numeric value - used as-is, no lookup required.
     * Legacy code: 'L'
     */
    LITERAL("L", "Literal numeric value"),
    
    /**
     * Working storage result/variable - holds calculated values.
     * Legacy code: 'R'
     */
    RESULT("R", "Working storage result variable"),
    
    /**
     * User exit - calls external routine.
     * Legacy code: 'X'
     */
    EXIT("X", "User exit call"),
    
    /**
     * Rate key lookup - retrieves value from rate tables.
     * Legacy code: '0' (resolved via JOIN on rate definition table)
     */
    RATE_KEY("K", "Rate key lookup"),
    
    /**
     * Database element - retrieves value from database.
     * Legacy code: '0' (resolved via JOIN on rate element table)
     */
    DATABASE("D", "Database element"),
    
    /**
     * Boolean/Conditional expression - controls flow based on condition.
     * Legacy code: '0' with CON_IND='Y'
     * Replaces the need for a separate conditional indicator field.
     */
    BOOLEAN("B", "Boolean/Conditional expression");
    
    private final String code;
    private final String description;
    
    ValueType(String code, String description)
    {
        this.code = code;
        this.description = description;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    /**
     * Returns true if this type represents a conditional/boolean expression.
     */
    public boolean isConditional()
    {
        return this == BOOLEAN;
    }
    
    /**
     * Returns true if this type requires an external lookup.
     */
    public boolean requiresLookup()
    {
        return this == CODE || this == RATE_KEY || this == DATABASE || this == EXIT;
    }
    
    /**
     * Get ValueType from its code.
     */
    public static ValueType fromCode(String code)
    {
        if (code == null || code.trim().isEmpty())
        {
            throw new IllegalArgumentException("ValueType code cannot be null or empty");
        }
        
        for (ValueType type : values())
        {
            if (type.code.equals(code.trim().toUpperCase()))
            {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ValueType code: " + code);
    }
    
    /**
     * Convert legacy WARP_VALUE_TYPE_CD to new ValueType.
     * Note: Legacy '0' cannot be converted without additional context.
     * Use fromLegacyWithContext() for '0' codes.
     * 
     * @param legacyCode The legacy code (C, L, R, X)
     * @return The corresponding ValueType
     * @throws IllegalArgumentException if code is '0' or unknown
     */
    public static ValueType fromLegacy(String legacyCode)
    {
        if (legacyCode == null)
        {
            throw new IllegalArgumentException("Legacy code cannot be null");
        }
        
        return switch (legacyCode.trim())
        {
            case "C" -> CODE;
            case "L" -> LITERAL;
            case "R" -> RESULT;
            case "X" -> EXIT;
            case "0" -> throw new IllegalArgumentException(
                "Legacy '0' code requires context to resolve. " +
                "Use fromLegacyZero() instead.");
            default -> throw new IllegalArgumentException(
                "Unknown legacy code: " + legacyCode);
        };
    }
    
    /**
     * Convert legacy '0' code to new ValueType using context.
     * 
     * @param isConditional true if CON_IND = 'Y'
     * @param isRateKey true if found in rate definition table
     * @param isDatabaseElement true if found in rate element table
     * @return The resolved ValueType
     */
    public static ValueType fromLegacyZero(
            boolean isConditional, 
            boolean isRateKey, 
            boolean isDatabaseElement)
    {
        if (isConditional)
        {
            return BOOLEAN;
        }
        if (isDatabaseElement)
        {
            return DATABASE;
        }
        if (isRateKey)
        {
            return RATE_KEY;
        }
        
        // Default to RATE_KEY if we can't determine
        // This matches legacy behavior of checking rate key last
        return RATE_KEY;
    }
    
    /**
     * Convert legacy code with context - handles both regular codes and '0'.
     * Combined method for use by AlgorithmStep.fromLegacy().
     * 
     * @param legacyCode The legacy code (C, L, R, X, or 0)
     * @param isRateKey true if element is a rate key
     * @param isDatabaseElement true if element is a database column
     * @return The resolved ValueType
     */
    public static ValueType fromLegacy(
            String legacyCode,
            boolean isRateKey,
            boolean isDatabaseElement)
    {
        if (legacyCode == null)
        {
            return LITERAL;  // Default for null
        }
        
        String code = legacyCode.trim();
        
        // Handle '0' code with context
        if ("0".equals(code))
        {
            return fromLegacyZero(false, isRateKey, isDatabaseElement);
        }
        
        // Handle standard codes
        return switch (code)
        {
            case "C" -> CODE;
            case "L" -> LITERAL;
            case "R" -> RESULT;
            case "X" -> EXIT;
            case "B" -> BOOLEAN;
            case "K" -> RATE_KEY;
            case "D" -> DATABASE;
            default -> LITERAL;  // Default for unknown
        };
    }
}
