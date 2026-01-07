package genowa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for WARP_CTL table.
 * Identifies a table assignment within an insurance line.
 */
@Embeddable
public class WarpCtlId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Column(name = "WARP_INS_LINE_CD", length = 3, nullable = false)
    private String insuranceLineCode;
    
    @Convert(converter = LevelTypeConverter.class)
    @Column(name = "LEVEL_TYPE_CD", length = 1, nullable = false)
    private LevelType levelType;
    
    @Column(name = "DB_TABLE_NM", length = 18, nullable = false)
    private String tableName;
    
    public WarpCtlId() {}
    
    public WarpCtlId(String insuranceLineCode, LevelType levelType, String tableName) {
        this.insuranceLineCode = insuranceLineCode;
        this.levelType = levelType;
        this.tableName = tableName;
    }
    
    // Getters and setters
    public String getInsuranceLineCode() { return insuranceLineCode; }
    public void setInsuranceLineCode(String insuranceLineCode) { this.insuranceLineCode = insuranceLineCode; }
    
    public LevelType getLevelType() { return levelType; }
    public void setLevelType(LevelType levelType) { this.levelType = levelType; }
    
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarpCtlId that = (WarpCtlId) o;
        return Objects.equals(insuranceLineCode, that.insuranceLineCode) &&
               levelType == that.levelType &&
               Objects.equals(tableName, that.tableName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(insuranceLineCode, levelType, tableName);
    }
}
