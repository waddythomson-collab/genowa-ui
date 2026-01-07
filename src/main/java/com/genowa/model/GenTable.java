package com.genowa.model;

import java.sql.Timestamp;

public class GenTable
{
    private Integer tableId;
    private String tableName;
    private String tableDesc;
    private Integer parentTableId;
    private String levelTypeCode;
    private Integer keyLength;
    private Integer dataLength;
    private String tableType;
    private String rateTableType;
    private String hasDetailYn;
    private String activeYn;
    private String createdBy;
    private Timestamp createdDate;
    private String modifiedBy;
    private Timestamp modifiedDate;

    // Default constructor
    public GenTable()
    {
    }

    // Getters and Setters
    public Integer getTableId()
    {
        return tableId;
    }

    public void setTableId(Integer tableId)
    {
        this.tableId = tableId;
    }

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public String getTableDesc()
    {
        return tableDesc;
    }

    public void setTableDesc(String tableDesc)
    {
        this.tableDesc = tableDesc;
    }

    public Integer getParentTableId()
    {
        return parentTableId;
    }

    public void setParentTableId(Integer parentTableId)
    {
        this.parentTableId = parentTableId;
    }

    public String getLevelTypeCode()
    {
        return levelTypeCode;
    }

    public void setLevelTypeCode(String levelTypeCode)
    {
        this.levelTypeCode = levelTypeCode;
    }

    public Integer getKeyLength()
    {
        return keyLength;
    }

    public void setKeyLength(Integer keyLength)
    {
        this.keyLength = keyLength;
    }

    public Integer getDataLength()
    {
        return dataLength;
    }

    public void setDataLength(Integer dataLength)
    {
        this.dataLength = dataLength;
    }

    public String getTableType()
    {
        return tableType;
    }

    public void setTableType(String tableType)
    {
        this.tableType = tableType;
    }

    public String getRateTableType()
    {
        return rateTableType;
    }

    public void setRateTableType(String rateTableType)
    {
        this.rateTableType = rateTableType;
    }

    public String getHasDetailYn()
    {
        return hasDetailYn;
    }

    public void setHasDetailYn(String hasDetailYn)
    {
        this.hasDetailYn = hasDetailYn;
    }

    public String getActiveYn()
    {
        return activeYn;
    }

    public void setActiveYn(String activeYn)
    {
        this.activeYn = activeYn;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate)
    {
        this.createdDate = createdDate;
    }

    public String getModifiedBy()
    {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }

    public Timestamp getModifiedDate()
    {
        return modifiedDate;
    }

    public void setModifiedDate(Timestamp modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public String toString()
    {
        return tableName + (tableDesc != null && !tableDesc.isEmpty() ? " - " + tableDesc : "");
    }
}
