package genowa.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a table definition in gen_tables (APG_TABLES).
 */
@Entity
@Table(name = "gen_tables")
public class GenTable
{
    @Id
    @Column(name = "table_index_nbr")
    private Integer tableIndexNbr;

    @Column(name = "table_nm", length = 18, nullable = false)
    private String tableNm;

    @Column(name = "table_type_cd", length = 1)
    private String tableTypeCd;

    @Column(name = "long_alias_nm", length = 80)
    private String longAliasNm;

    @OneToMany(mappedBy = "genTable", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GenField> fields = new ArrayList<>();

    // Constructors
    public GenTable()
    {
    }

    public GenTable(Integer tableIndexNbr, String tableNm)
    {
        this.tableIndexNbr = tableIndexNbr;
        this.tableNm = tableNm;
    }

    // Getters and setters
    public Integer getTableIndexNbr()
    {
        return tableIndexNbr;
    }

    public void setTableIndexNbr(Integer tableIndexNbr)
    {
        this.tableIndexNbr = tableIndexNbr;
    }

    public String getTableNm()
    {
        return tableNm;
    }

    public void setTableNm(String tableNm)
    {
        this.tableNm = tableNm;
    }

    public String getTableTypeCd()
    {
        return tableTypeCd;
    }

    public void setTableTypeCd(String tableTypeCd)
    {
        this.tableTypeCd = tableTypeCd;
    }

    public String getLongAliasNm()
    {
        return longAliasNm;
    }

    public void setLongAliasNm(String longAliasNm)
    {
        this.longAliasNm = longAliasNm;
    }

    public List<GenField> getFields()
    {
        return fields;
    }

    public void setFields(List<GenField> fields)
    {
        this.fields = fields;
    }

    public void addField(GenField field)
    {
        fields.add(field);
        field.setGenTable(this);
    }

    @Override
    public String toString()
    {
        return tableNm;
    }
}
