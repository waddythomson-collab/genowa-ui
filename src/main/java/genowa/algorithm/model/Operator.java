package genowa.algorithm.model;

/**
 * Mathematical operators used in algorithm steps.
 * 
 * Maps to legacy WARP_OPERAND_CD values.
 */
public enum Operator
{
    /**
     * No operator - used for first step or standalone assignments.
     * Legacy code: blank or null
     */
    NONE("", ""),
    
    /**
     * Addition.
     * Legacy code: '+'
     */
    ADD("+", "+"),
    
    /**
     * Subtraction.
     * Legacy code: '-'
     */
    SUBTRACT("-", "-"),
    
    /**
     * Multiplication.
     * Legacy code: '*'
     */
    MULTIPLY("*", "*"),
    
    /**
     * Division.
     * Legacy code: '/'
     */
    DIVIDE("/", "/"),
    
    /**
     * Assignment - store value in result variable.
     * Legacy code: '='
     */
    ASSIGN("=", "="),
    
    /**
     * Minimum - take lesser of two values.
     * Legacy code: 'N'
     */
    MINIMUM("N", "MIN"),
    
    /**
     * Maximum - take greater of two values.
     * Legacy code: 'X'
     */
    MAXIMUM("X", "MAX"),
    
    /**
     * Equal comparison (for conditionals).
     * Legacy code: 'E'
     */
    EQUAL("E", "=="),
    
    /**
     * Not equal comparison (for conditionals).
     * Legacy code: 'Q'
     */
    NOT_EQUAL("Q", "!="),
    
    /**
     * Greater than comparison (for conditionals).
     * Legacy code: 'G'
     */
    GREATER_THAN("G", ">"),
    
    /**
     * Less than comparison (for conditionals).
     * Legacy code: 'L'
     */
    LESS_THAN("L", "<"),
    
    /**
     * Greater than or equal comparison (for conditionals).
     * Legacy code: 'H'
     */
    GREATER_EQUAL("H", ">="),
    
    /**
     * Less than or equal comparison (for conditionals).
     * Legacy code: 'K'
     */
    LESS_EQUAL("K", "<=");
    
    private final String legacyCode;
    private final String symbol;
    
    Operator(String legacyCode, String symbol)
    {
        this.legacyCode = legacyCode;
        this.symbol = symbol;
    }
    
    /**
     * Get the legacy WARP_OPERAND_CD value.
     */
    public String getLegacyCode()
    {
        return legacyCode;
    }
    
    /**
     * Get the display symbol for this operator.
     */
    public String getSymbol()
    {
        return symbol;
    }
    
    /**
     * Returns true if this is a comparison operator (used in conditionals).
     */
    public boolean isComparison()
    {
        return this == EQUAL || this == NOT_EQUAL || 
               this == GREATER_THAN || this == LESS_THAN ||
               this == GREATER_EQUAL || this == LESS_EQUAL;
    }
    
    /**
     * Returns true if this is an arithmetic operator.
     */
    public boolean isArithmetic()
    {
        return this == ADD || this == SUBTRACT || 
               this == MULTIPLY || this == DIVIDE;
    }
    
    /**
     * Convert legacy WARP_OPERAND_CD to Operator.
     * 
     * @param legacyCode The legacy operand code
     * @return The corresponding Operator, or NONE if null/blank
     */
    public static Operator fromLegacy(String legacyCode)
    {
        if (legacyCode == null || legacyCode.trim().isEmpty())
        {
            return NONE;
        }
        
        String code = legacyCode.trim();
        
        for (Operator op : values())
        {
            if (op.legacyCode.equals(code))
            {
                return op;
            }
        }
        
        // Unknown code - log warning and return NONE
        System.err.println("Warning: Unknown operator code '" + code + "', defaulting to NONE");
        return NONE;
    }
}
