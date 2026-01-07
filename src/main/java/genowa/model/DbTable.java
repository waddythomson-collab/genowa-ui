package genowa.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a database table definition (APG_TABLES).
 * Tables contain columns that can be used as Database Elements in algorithms.
 */
@Entity
@Table(name = "gen_tables")
public class DbTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TABLE_ID")
    private Long id;

    @Column(name = "TABLE_NM", length = 18, nullable = false)
    private String tableName;

    @Column(name = "TABLE_INDEX_NBR")
    private Integer tableIndex;

    @Column(name = "TABLE_DESC", length = 100)
    private String description;

    @Column(name = "INSURANCE_LINE_CD", length = 2)
    private String insuranceLine;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DbColumn> columns = new ArrayList<>();

    // Constructors
    public DbTable() {}

    public DbTable(String tableName) {
        this.tableName = tableName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public Integer getTableIndex() { return tableIndex; }
    public void setTableIndex(Integer tableIndex) { this.tableIndex = tableIndex; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInsuranceLine() { return insuranceLine; }
    public void setInsuranceLine(String insuranceLine) { this.insuranceLine = insuranceLine; }

    public List<DbColumn> getColumns() { return columns; }
    public void setColumns(List<DbColumn> columns) { this.columns = columns; }

    public void addColumn(DbColumn column) {
        columns.add(column);
        column.setTable(this);
    }

    @Override
    public String toString() {
        return tableName;
    }
}
