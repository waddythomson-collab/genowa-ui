package genowa.model;

import jakarta.persistence.*;

/**
 * Maps Element Values to specific columns for tables within an insurance line.
 * 
 * This is the core configuration that allows the code generator to know:
 * - Which column contains the "Primary Break" (e.g., vehicle unit number)
 * - Which column contains the "Premium Column" (e.g., total premium)
 * - Which column contains the "Coverage Code"
 * - etc.
 * 
 * Level Type Codes:
 *   '$' = Premium tables
 *   'F' = Form tables  
 *   'P' = Primary (Rating) tables
 *   'C' = Coverage tables
 *   'S' = Support tables
 */
@Entity
@Table(name = "gen_ctl_data")
public class WarpCtlData {

    @EmbeddedId
    private WarpCtlDataId id;

    @Column(name = "DB_COL_NM", length = 18)
    private String columnName;
    
    /**
     * Reference to parent WARP_CTL record.
     * Uses the first 3 columns of the composite key.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "WARP_INS_LINE_CD", referencedColumnName = "WARP_INS_LINE_CD", insertable = false, updatable = false),
        @JoinColumn(name = "LEVEL_TYPE_CD", referencedColumnName = "LEVEL_TYPE_CD", insertable = false, updatable = false),
        @JoinColumn(name = "DB_TABLE_NM", referencedColumnName = "DB_TABLE_NM", insertable = false, updatable = false)
    })
    private WarpCtl warpCtl;

    // Constructors
    public WarpCtlData() {
        this.id = new WarpCtlDataId();
    }

    public WarpCtlData(String insuranceLineCode, String levelTypeCode,
                       String tableName, String controlCode, String columnName) {
        this.id = new WarpCtlDataId(insuranceLineCode, levelTypeCode, tableName, controlCode);
        this.columnName = columnName;
    }

    // Getters and Setters
    public WarpCtlDataId getId() { return id; }
    public void setId(WarpCtlDataId id) { this.id = id; }

    public String getColumnName() { return columnName; }
    public void setColumnName(String columnName) { this.columnName = columnName; }

    // Convenience getters that delegate to the ID
    public String getInsuranceLineCode() { return id.getInsuranceLineCode(); }
    public void setInsuranceLineCode(String code) { id.setInsuranceLineCode(code); }

    public String getLevelTypeCode() { return id.getLevelTypeCode(); }
    public void setLevelTypeCode(String code) { id.setLevelTypeCode(code); }

    public String getTableName() { return id.getTableName(); }
    public void setTableName(String name) { id.setTableName(name); }

    public String getControlCode() { return id.getControlCode(); }
    public void setControlCode(String code) { id.setControlCode(code); }
    
    public WarpCtl getWarpCtl() { return warpCtl; }
    public void setWarpCtl(WarpCtl warpCtl) { this.warpCtl = warpCtl; }

    /**
     * Returns the human-readable level type name.
     */
    public String getLevelTypeName() {
        return switch (id.getLevelTypeCode()) {
            case "$" -> "Premium";
            case "F" -> "Form";
            case "P" -> "Primary";
            case "C" -> "Coverage";
            case "S" -> "Support";
            default -> "Unknown";
        };
    }

    @Override
    public String toString() {
        return String.format("%s.%s -> %s", 
            id.getTableName(), id.getControlCode(), columnName);
    }
}
