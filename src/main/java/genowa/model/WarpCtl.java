package genowa.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity for gen_ctl table.
 * Defines the table hierarchy for each Insurance Line - which tables
 * are assigned and their parent/child relationships for the tree structure.
 * 
 * Each row represents one table assignment with:
 * - Insurance Line (e.g., "PA" for Personal Auto)
 * - Level Type (Premium, Form, Primary, Coverage, Support)
 * - Table name from GEN_TABLES
 * - Hierarchy position (level, sub-level, parent)
 */
@Entity
@Table(name = "gen_ctl")
public class WarpCtl {
    
    @EmbeddedId
    private WarpCtlId id;
    
    /**
     * Primary level in hierarchy (1 = root level).
     */
    @Column(name = "DB_CTL_LEVEL_NBR")
    private Integer level;
    
    /**
     * Sub-level for ordering siblings at the same level.
     */
    @Column(name = "DB_SUB_LEVEL_NBR")
    private Integer subLevel;
    
    /**
     * Parent table name (null for root tables).
     */
    @Column(name = "DB_PARENT_NM", length = 18)
    private String parentTableName;
    
    /**
     * Element value mappings for this table.
     * Maps control codes to actual column names.
     */
    @OneToMany(mappedBy = "warpCtl", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<WarpCtlData> elementMappings = new ArrayList<>();
    
    public WarpCtl() {}
    
    public WarpCtl(String insuranceLineCode, LevelType levelType, String tableName) {
        this.id = new WarpCtlId(insuranceLineCode, levelType, tableName);
    }
    
    // Convenience getters that delegate to ID
    public String getInsuranceLineCode() { 
        return id != null ? id.getInsuranceLineCode() : null; 
    }
    
    public LevelType getLevelType() { 
        return id != null ? id.getLevelType() : null; 
    }
    
    public String getTableName() { 
        return id != null ? id.getTableName() : null; 
    }
    
    // ID getter/setter
    public WarpCtlId getId() { return id; }
    public void setId(WarpCtlId id) { this.id = id; }
    
    // Hierarchy getters/setters
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    
    public Integer getSubLevel() { return subLevel; }
    public void setSubLevel(Integer subLevel) { this.subLevel = subLevel; }
    
    public String getParentTableName() { return parentTableName; }
    public void setParentTableName(String parentTableName) { this.parentTableName = parentTableName; }
    
    // Element mappings
    public List<WarpCtlData> getElementMappings() { return elementMappings; }
    public void setElementMappings(List<WarpCtlData> elementMappings) { this.elementMappings = elementMappings; }
    
    /**
     * Add an element value mapping to this table.
     */
    public void addElementMapping(WarpCtlData mapping) {
        elementMappings.add(mapping);
        mapping.setWarpCtl(this);
    }
    
    /**
     * Remove an element value mapping.
     */
    public void removeElementMapping(WarpCtlData mapping) {
        elementMappings.remove(mapping);
        mapping.setWarpCtl(null);
    }
    
    /**
     * Check if this is a root table (no parent).
     */
    public boolean isRoot() {
        return parentTableName == null || parentTableName.isBlank();
    }
    
    /**
     * Get display name for tree view.
     */
    public String getDisplayName() {
        return getTableName();
    }
    
    @Override
    public String toString() {
        return String.format("WarpCtl[%s/%s/%s level=%d]", 
            getInsuranceLineCode(), getLevelType(), getTableName(), level);
    }
}
