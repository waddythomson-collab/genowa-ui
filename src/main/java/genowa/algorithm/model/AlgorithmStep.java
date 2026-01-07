package genowa.algorithm.model;

import java.math.BigDecimal;

/**
 * Represents a single step in an algorithm.
 * 
 * Maps to WARP_ALGORITHM table rows but with resolved types.
 */
public class AlgorithmStep
{
    private int sequence;
    private int stepType;  // 0=calc, 1=conditional, etc.
    private String elementId;
    private String description;
    
    private ValueType valueType;
    private Operator operator;
    private RoundingType rounding;
    private int decimalPlaces;
    
    // Literal value (when valueType = LITERAL)
    private BigDecimal literalValue;
    
    // Code reference (when valueType = CODE)
    private String codeTable;
    private String codeValue;
    
    // Database reference (when valueType = DATABASE)
    private String dbTable;
    private String dbColumn;
    
    // Control flow (for conditional steps)
    private int targetStep;
    private String compareElementId;
    
    // Default constructor
    public AlgorithmStep()
    {
        this.operator = Operator.NONE;
        this.valueType = ValueType.LITERAL;
        this.rounding = RoundingType.NONE;
        this.decimalPlaces = 0;
    }
    
    // Builder-style constructor for common cases
    public AlgorithmStep(int sequence, Operator operator, String elementId,
                         ValueType valueType, RoundingType rounding,
                         int decimalPlaces, String description)
    {
        this.sequence = sequence;
        this.operator = operator != null ? operator : Operator.NONE;
        this.elementId = elementId;
        this.valueType = valueType != null ? valueType : ValueType.LITERAL;
        this.rounding = rounding != null ? rounding : RoundingType.NONE;
        this.decimalPlaces = decimalPlaces;
        this.description = description;
    }
    
    // ========== Getters and Setters ==========
    
    public int getSequence() { return sequence; }
    public void setSequence(int sequence) { this.sequence = sequence; }
    
    public int getStepType() { return stepType; }
    public void setStepType(int stepType) { this.stepType = stepType; }
    
    public String getElementId() { return elementId; }
    public void setElementId(String elementId) { this.elementId = elementId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ValueType getValueType() { return valueType; }
    public void setValueType(ValueType valueType) { this.valueType = valueType; }
    
    public Operator getOperator() { return operator; }
    public void setOperator(Operator operator) { this.operator = operator; }
    
    public RoundingType getRounding() { return rounding; }
    public void setRounding(RoundingType rounding) { this.rounding = rounding; }
    
    public int getDecimalPlaces() { return decimalPlaces; }
    public void setDecimalPlaces(int decimalPlaces) { this.decimalPlaces = decimalPlaces; }
    
    public BigDecimal getLiteralValue() { return literalValue; }
    public void setLiteralValue(BigDecimal literalValue) { this.literalValue = literalValue; }
    
    public String getCodeTable() { return codeTable; }
    public void setCodeTable(String codeTable) { this.codeTable = codeTable; }
    
    public String getCodeValue() { return codeValue; }
    public void setCodeValue(String codeValue) { this.codeValue = codeValue; }
    
    public String getDbTable() { return dbTable; }
    public void setDbTable(String dbTable) { this.dbTable = dbTable; }
    
    public String getDbColumn() { return dbColumn; }
    public void setDbColumn(String dbColumn) { this.dbColumn = dbColumn; }
    
    public int getTargetStep() { return targetStep; }
    public void setTargetStep(int targetStep) { this.targetStep = targetStep; }
    
    public String getCompareElementId() { return compareElementId; }
    public void setCompareElementId(String compareElementId) { this.compareElementId = compareElementId; }
    
    // ========== Legacy Conversion ==========
    
    /**
     * Factory method to create from legacy database values.
     * 
     * @param sequence step sequence number
     * @param operandCd operand code ('+', '-', '*', '/', '=', etc.)
     * @param element element name
     * @param valueTypeCd value type code ('L', 'C', 'R', 'E', 'X')
     * @param conInd condition indicator ('Y'/'N' or blank)
     * @param roundTypeCd rounding type code ('D', 'W', or blank)
     * @param roundValue rounding precision
     * @param comment step comment/description
     * @param isRateKey true if element is a rate key
     * @param isDatabaseElement true if element is a database column reference
     * @return configured AlgorithmStep
     */
    public static AlgorithmStep fromLegacy(
            int sequence,
            String operandCd,
            String element,
            String valueTypeCd,
            String conInd,
            String roundTypeCd,
            int roundValue,
            String comment,
            boolean isRateKey,
            boolean isDatabaseElement)
    {
        AlgorithmStep step = new AlgorithmStep();
        
        step.sequence = sequence;
        step.elementId = element;
        step.description = comment;
        step.operator = Operator.fromLegacy(operandCd);
        step.rounding = RoundingType.fromLegacy(roundTypeCd);
        step.decimalPlaces = roundValue;
        
        // Determine step type from condition indicator
        boolean isConditional = "Y".equalsIgnoreCase(conInd);
        step.stepType = isConditional ? 1 : 0;
        
        // Resolve value type using legacy code + context
        step.valueType = ValueType.fromLegacy(valueTypeCd, isRateKey, isDatabaseElement);
        
        // Handle literal values - element may contain the literal
        if (step.valueType == ValueType.LITERAL && element != null)
        {
            try
            {
                step.literalValue = new BigDecimal(element);
            }
            catch (NumberFormatException e)
            {
                // Not a numeric literal - keep as element reference
            }
        }
        
        return step;
    }
    
    /**
     * Checks if this is a conditional/branching step.
     */
    public boolean isConditional()
    {
        return stepType == 1 || (valueType != null && valueType.isConditional());
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%04d: ", sequence));
        
        if (operator != null && operator != Operator.NONE)
        {
            sb.append(operator.getSymbol()).append(" ");
        }
        
        sb.append(elementId);
        sb.append(" [").append(valueType).append("]");
        
        if (rounding != null && rounding != RoundingType.NONE)
        {
            sb.append(" Round:").append(rounding);
            if (rounding == RoundingType.DECIMAL)
            {
                sb.append("(").append(decimalPlaces).append(")");
            }
        }
        
        if (description != null && !description.isEmpty())
        {
            sb.append(" // ").append(description);
        }
        
        return sb.toString();
    }
}
