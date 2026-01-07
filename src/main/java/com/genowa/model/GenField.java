package com.genowa.model;

import java.sql.Timestamp;

public class GenField
{
    private Integer fieldId;
    private Integer tableId;
    private String fieldName;
    private String fieldDesc;
    private Integer fieldSeq;
    private String dataType;
    private Integer fieldLength;
    private Integer decimalPlaces;
    private String keyFieldYn;
    private String requiredYn;
    private String activeYn;
    private String createdBy;
    private Timestamp createdDate;
    private String modifiedBy;
    private Timestamp modifiedDate;

    // Default constructor
    public GenField()
    {
    }

    // Getters and Setters
    public Integer getFieldId()
    {
        return fieldId;
    }

    public void setFieldId(Integer fieldId)
    {
        this.fieldId = fieldId;
    }

    public Integer getTableId()
    {
        return tableId;
    }

    public void setTableId(Integer tableId)
    {
        this.tableId = tableId;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public String getFieldDesc()
    {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc)
    {
        this.fieldDesc = fieldDesc;
    }

    public Integer getFieldSeq()
    {
        return fieldSeq;
    }

    public void setFieldSeq(Integer fieldSeq)
    {
        this.fieldSeq = fieldSeq;
    }

    public String getDataType()
    {
        return dataType;
    }

    public void setDataType(String dataType)
    {
        this.dataType = dataType;
    }

    public Integer getFieldLength()
    {
        return fieldLength;
    }

    public void setFieldLength(Integer fieldLength)
    {
        this.fieldLength = fieldLength;
    }

    public Integer getDecimalPlaces()
    {
        return decimalPlaces;
    }

    public void setDecimalPlaces(Integer decimalPlaces)
    {
        this.decimalPlaces = decimalPlaces;
    }

    public String getKeyFieldYn()
    {
        return keyFieldYn;
    }

    public void setKeyFieldYn(String keyFieldYn)
    {
        this.keyFieldYn = keyFieldYn;
    }

    public String getRequiredYn()
    {
        return requiredYn;
    }

    public void setRequiredYn(String requiredYn)
    {
        this.requiredYn = requiredYn;
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
        return fieldName + (fieldDesc != null && !fieldDesc.isEmpty() ? " - " + fieldDesc : "");
    }
}
