package genowa.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * JPA Entity for gen_algorithm_step table.
 * Stores individual steps within an algorithm.
 */
@Entity
@Table(name = "gen_algorithm_step")
public class GenAlgorithmStep {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gen_algorithm_step_id")
    private Integer stepId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gen_algorithm_id", nullable = false)
    private GenAlgorithm algorithm;
    
    @Column(name = "sequence", nullable = false)
    private Integer sequence;
    
    @Column(name = "step_type")
    private Integer stepType; // 0=calc, 1=conditional, etc.
    
    @Column(name = "element_id", length = 100)
    private String elementId;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "value_type_cd", length = 10)
    private String valueTypeCode; // L, C, R, K, D, B, X
    
    @Column(name = "operator_cd", length = 10)
    private String operatorCode; // +, -, *, /, =, ^
    
    @Column(name = "rounding_type_cd", length = 10)
    private String roundingTypeCode; // 0, W, D, N, A, O
    
    @Column(name = "decimal_places")
    private Integer decimalPlaces;
    
    // Literal value
    @Column(name = "literal_value", precision = 18, scale = 6)
    private BigDecimal literalValue;
    
    // Code reference
    @Column(name = "code_table", length = 50)
    private String codeTable;
    
    @Column(name = "code_value", length = 50)
    private String codeValue;
    
    // Database reference
    @Column(name = "db_table", length = 50)
    private String dbTable;
    
    @Column(name = "db_column", length = 50)
    private String dbColumn;
    
    // Control flow (for conditional steps)
    @Column(name = "target_step")
    private Integer targetStep;
    
    @Column(name = "compare_element_id", length = 100)
    private String compareElementId;
    
    // Constructors
    public GenAlgorithmStep() {
    }
    
    public GenAlgorithmStep(GenAlgorithm algorithm, Integer sequence) {
        this.algorithm = algorithm;
        this.sequence = sequence;
    }
    
    // Getters and Setters
    public Integer getStepId() {
        return stepId;
    }
    
    public void setStepId(Integer stepId) {
        this.stepId = stepId;
    }
    
    public GenAlgorithm getAlgorithm() {
        return algorithm;
    }
    
    public void setAlgorithm(GenAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
    
    public Integer getSequence() {
        return sequence;
    }
    
    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
    
    public Integer getStepType() {
        return stepType;
    }
    
    public void setStepType(Integer stepType) {
        this.stepType = stepType;
    }
    
    public String getElementId() {
        return elementId;
    }
    
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getValueTypeCode() {
        return valueTypeCode;
    }
    
    public void setValueTypeCode(String valueTypeCode) {
        this.valueTypeCode = valueTypeCode;
    }
    
    public String getOperatorCode() {
        return operatorCode;
    }
    
    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }
    
    public String getRoundingTypeCode() {
        return roundingTypeCode;
    }
    
    public void setRoundingTypeCode(String roundingTypeCode) {
        this.roundingTypeCode = roundingTypeCode;
    }
    
    public Integer getDecimalPlaces() {
        return decimalPlaces;
    }
    
    public void setDecimalPlaces(Integer decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }
    
    public BigDecimal getLiteralValue() {
        return literalValue;
    }
    
    public void setLiteralValue(BigDecimal literalValue) {
        this.literalValue = literalValue;
    }
    
    public String getCodeTable() {
        return codeTable;
    }
    
    public void setCodeTable(String codeTable) {
        this.codeTable = codeTable;
    }
    
    public String getCodeValue() {
        return codeValue;
    }
    
    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }
    
    public String getDbTable() {
        return dbTable;
    }
    
    public void setDbTable(String dbTable) {
        this.dbTable = dbTable;
    }
    
    public String getDbColumn() {
        return dbColumn;
    }
    
    public void setDbColumn(String dbColumn) {
        this.dbColumn = dbColumn;
    }
    
    public Integer getTargetStep() {
        return targetStep;
    }
    
    public void setTargetStep(Integer targetStep) {
        this.targetStep = targetStep;
    }
    
    public String getCompareElementId() {
        return compareElementId;
    }
    
    public void setCompareElementId(String compareElementId) {
        this.compareElementId = compareElementId;
    }
}

