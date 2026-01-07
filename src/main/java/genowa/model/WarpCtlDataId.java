package genowa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for WARP_CTL_DATA.
 * Identifies a specific Element Value mapping for a table within an insurance line.
 */
@Embeddable
public class WarpCtlDataId implements Serializable {

    @Column(name = "WARP_INS_LINE_CD", length = 3, nullable = false)
    private String insuranceLineCode;

    @Column(name = "LEVEL_TYPE_CD", length = 1, nullable = false)
    private String levelTypeCode;

    @Column(name = "DB_TABLE_NM", length = 18, nullable = false)
    private String tableName;

    @Column(name = "WARP_CTL_CD", length = 18, nullable = false)
    private String controlCode;

    // Constructors
    public WarpCtlDataId() {}

    public WarpCtlDataId(String insuranceLineCode, String levelTypeCode, 
                         String tableName, String controlCode) {
        this.insuranceLineCode = insuranceLineCode;
        this.levelTypeCode = levelTypeCode;
        this.tableName = tableName;
        this.controlCode = controlCode;
    }
    
    /**
     * Convenience constructor using enums.
     */
    public WarpCtlDataId(String insuranceLineCode, LevelType levelType, 
                         String tableName, ElementValue elementValue) {
        this.insuranceLineCode = insuranceLineCode;
        this.levelTypeCode = levelType.getCode();
        this.tableName = tableName;
        this.controlCode = elementValue.getCode();
    }

    // Getters and Setters
    public String getInsuranceLineCode() { return insuranceLineCode; }
    public void setInsuranceLineCode(String insuranceLineCode) { 
        this.insuranceLineCode = insuranceLineCode; 
    }

    public String getLevelTypeCode() { return levelTypeCode; }
    public void setLevelTypeCode(String levelTypeCode) { 
        this.levelTypeCode = levelTypeCode; 
    }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { 
        this.tableName = tableName; 
    }

    public String getControlCode() { return controlCode; }
    public void setControlCode(String controlCode) { 
        this.controlCode = controlCode; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarpCtlDataId that = (WarpCtlDataId) o;
        return Objects.equals(insuranceLineCode, that.insuranceLineCode) &&
               Objects.equals(levelTypeCode, that.levelTypeCode) &&
               Objects.equals(tableName, that.tableName) &&
               Objects.equals(controlCode, that.controlCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(insuranceLineCode, levelTypeCode, tableName, controlCode);
    }
}
