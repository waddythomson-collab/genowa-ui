package genowa.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for GenField (table_index_nbr + key_cct_nbr).
 */
public class GenFieldId implements Serializable
{
    private Integer tableIndexNbr;
    private Integer keyCctNbr;

    public GenFieldId()
    {
    }

    public GenFieldId(Integer tableIndexNbr, Integer keyCctNbr)
    {
        this.tableIndexNbr = tableIndexNbr;
        this.keyCctNbr = keyCctNbr;
    }

    public Integer getTableIndexNbr()
    {
        return tableIndexNbr;
    }

    public void setTableIndexNbr(Integer tableIndexNbr)
    {
        this.tableIndexNbr = tableIndexNbr;
    }

    public Integer getKeyCctNbr()
    {
        return keyCctNbr;
    }

    public void setKeyCctNbr(Integer keyCctNbr)
    {
        this.keyCctNbr = keyCctNbr;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenFieldId that = (GenFieldId) o;
        return Objects.equals(tableIndexNbr, that.tableIndexNbr) &&
               Objects.equals(keyCctNbr, that.keyCctNbr);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(tableIndexNbr, keyCctNbr);
    }
}
