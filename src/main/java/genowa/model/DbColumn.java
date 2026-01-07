package genowa.model;

import jakarta.persistence.*;

/**
 * Represents a database column/field definition (APG_FIELDS).
 * Columns are the Database Elements that can be used in algorithms and conditionals.
 */
@Entity
@Table(name = "gen_fields")
public class DbColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COLUMN_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TABLE_ID", nullable = false)
    private DbTable table;

    @Column(name = "COLUMN_NM", length = 18, nullable = false)
    private String columnName;

    @Column(name = "COLUMN_DESC", length = 100)
    private String description;

    @Column(name = "DATA_TYPE_CD", length = 1)
    private String dataType; // C=Character, N=Numeric (UShort), D=Double

    @Column(name = "FLD_LENGTH_NBR")
    private Integer fieldLength;

    @Column(name = "DECIMALS_NBR")
    private Integer decimals;

    @Column(name = "COBOL_NM", length = 30)
    private String cobolName;

    @Column(name = "KEY_FLAG_CD", length = 1)
    private String keyFlag; // Y/N - is this a key column?

    @Column(name = "DATE_FLAG_CD", length = 1)
    private String dateFlag; // Y/N - is this a date field?

    @Column(name = "REQUIRED_IND", length = 1)
    private String requiredInd; // Y/N - is value required?

    @Column(name = "DELETED_IND", length = 1)
    private String deletedInd; // Y/N - soft delete

    // Column Options - what operations are allowed
    @Column(name = "IO_IND", length = 1)
    private String ioInd = "Y";

    @Column(name = "MINIMUM_IND", length = 1)
    private String minimumInd = "N";

    @Column(name = "MAXIMUM_IND", length = 1)
    private String maximumInd = "N";

    @Column(name = "SUM_IND", length = 1)
    private String sumInd = "N";

    @Column(name = "COUNT_IND", length = 1)
    private String countInd = "N";

    @Column(name = "CURSOR_IND", length = 1)
    private String cursorInd = "N";

    @Column(name = "READ_IND", length = 1)
    private String readInd = "N";

    @Column(name = "KEYSET_OVERRIDE_IND", length = 1)
    private String keysetOverrideInd = "N";

    @Column(name = "EXISTS_IND", length = 1)
    private String existsInd = "N";

    @Column(name = "CURSOR_RESULT_IND", length = 1)
    private String cursorResultInd = "N";

    // Constructors
    public DbColumn() {}

    public DbColumn(String columnName, String dataType, Integer fieldLength) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.fieldLength = fieldLength;
        this.cobolName = columnName.replace("_", "-"); // Default COBOL name
    }

    // Helper methods
    public String getDataTypeDisplay() {
        return switch (dataType) {
            case "C" -> "Character";
            case "N" -> "UShort";
            case "D" -> "Double";
            default -> dataType;
        };
    }

    public String getLengthDisplay() {
        if ("D".equals(dataType) && decimals != null && decimals > 0) {
            return fieldLength + "." + decimals;
        }
        return fieldLength != null ? fieldLength.toString() : "";
    }

    public boolean isKey() {
        return "Y".equals(keyFlag);
    }

    public boolean isRequired() {
        return "Y".equals(requiredInd);
    }

    public boolean isDeleted() {
        return "Y".equals(deletedInd);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DbTable getTable() { return table; }
    public void setTable(DbTable table) { this.table = table; }

    public String getColumnName() { return columnName; }
    public void setColumnName(String columnName) { this.columnName = columnName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public Integer getFieldLength() { return fieldLength; }
    public void setFieldLength(Integer fieldLength) { this.fieldLength = fieldLength; }

    public Integer getDecimals() { return decimals; }
    public void setDecimals(Integer decimals) { this.decimals = decimals; }

    public String getCobolName() { return cobolName; }
    public void setCobolName(String cobolName) { this.cobolName = cobolName; }

    public String getKeyFlag() { return keyFlag; }
    public void setKeyFlag(String keyFlag) { this.keyFlag = keyFlag; }

    public String getDateFlag() { return dateFlag; }
    public void setDateFlag(String dateFlag) { this.dateFlag = dateFlag; }

    public String getRequiredInd() { return requiredInd; }
    public void setRequiredInd(String requiredInd) { this.requiredInd = requiredInd; }

    public String getDeletedInd() { return deletedInd; }
    public void setDeletedInd(String deletedInd) { this.deletedInd = deletedInd; }

    public String getIoInd() { return ioInd; }
    public void setIoInd(String ioInd) { this.ioInd = ioInd; }

    public String getMinimumInd() { return minimumInd; }
    public void setMinimumInd(String minimumInd) { this.minimumInd = minimumInd; }

    public String getMaximumInd() { return maximumInd; }
    public void setMaximumInd(String maximumInd) { this.maximumInd = maximumInd; }

    public String getSumInd() { return sumInd; }
    public void setSumInd(String sumInd) { this.sumInd = sumInd; }

    public String getCountInd() { return countInd; }
    public void setCountInd(String countInd) { this.countInd = countInd; }

    public String getCursorInd() { return cursorInd; }
    public void setCursorInd(String cursorInd) { this.cursorInd = cursorInd; }

    public String getReadInd() { return readInd; }
    public void setReadInd(String readInd) { this.readInd = readInd; }

    public String getKeysetOverrideInd() { return keysetOverrideInd; }
    public void setKeysetOverrideInd(String keysetOverrideInd) { this.keysetOverrideInd = keysetOverrideInd; }

    public String getExistsInd() { return existsInd; }
    public void setExistsInd(String existsInd) { this.existsInd = existsInd; }

    public String getCursorResultInd() { return cursorResultInd; }
    public void setCursorResultInd(String cursorResultInd) { this.cursorResultInd = cursorResultInd; }

    @Override
    public String toString() {
        return columnName;
    }
}
