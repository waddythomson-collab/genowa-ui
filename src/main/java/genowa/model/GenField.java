package genowa.model;

import jakarta.persistence.*;

/**
 * Entity representing a field/column definition in gen_fields (APG_FIELDS).
 */
@Entity
@Table(name = "gen_fields")
@IdClass(GenFieldId.class)
public class GenField
{
    @Id
    @Column(name = "table_index_nbr")
    private Integer tableIndexNbr;

    @Id
    @Column(name = "key_cct_nbr")
    private Integer keyCctNbr;

    @Column(name = "column_nm", length = 18, nullable = false)
    private String columnNm;

    @Column(name = "data_type_nm", length = 1, nullable = false)
    private String dataTypeNm;

    @Column(name = "key_flag_cd", length = 1)
    private String keyFlagCd;

    @Column(name = "fld_length_nbr")
    private Integer fldLengthNbr;

    @Column(name = "c_nm", length = 30)
    private String cNm;

    @Column(name = "cobol_nm", length = 30)
    private String cobolNm;

    @Column(name = "req_flag_cd", length = 1)
    private String reqFlagCd;

    @Column(name = "money_flag_cd", length = 1)
    private String moneyFlagCd;

    @Column(name = "decimals_nbr")
    private Integer decimalsNbr;

    @Column(name = "range_flag_cd", length = 1)
    private String rangeFlagCd;

    @Column(name = "range_low_cd", length = 30)
    private String rangeLowCd;

    @Column(name = "range_high_cd", length = 30)
    private String rangeHighCd;

    @Column(name = "table_val_flag_cd", length = 1)
    private String tableValFlagCd;

    @Column(name = "table_val_file_nbr")
    private Integer tableValFileNbr;

    @Column(name = "date_flag_cd", length = 1)
    private String dateFlagCd;

    @Column(name = "sec_field_flag_cd", length = 1)
    private String secFieldFlagCd;

    @Column(name = "alpha_flag_cd", length = 1)
    private String alphaFlagCd;

    @Column(name = "upper_case_flag_cd", length = 1)
    private String upperCaseFlagCd;

    @Column(name = "derived_flag_cd", length = 1)
    private String derivedFlagCd;

    @Column(name = "default_value_cd", length = 30)
    private String defaultValueCd;

    @Column(name = "long_alias_nm", length = 80)
    private String longAliasNm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_index_nbr", insertable = false, updatable = false)
    private GenTable genTable;

    // Constructors
    public GenField()
    {
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

    public Integer getKeyCctNbr()
    {
        return keyCctNbr;
    }

    public void setKeyCctNbr(Integer keyCctNbr)
    {
        this.keyCctNbr = keyCctNbr;
    }

    public String getColumnNm()
    {
        return columnNm;
    }

    public void setColumnNm(String columnNm)
    {
        this.columnNm = columnNm;
    }

    public String getDataTypeNm()
    {
        return dataTypeNm;
    }

    public void setDataTypeNm(String dataTypeNm)
    {
        this.dataTypeNm = dataTypeNm;
    }

    public String getKeyFlagCd()
    {
        return keyFlagCd;
    }

    public void setKeyFlagCd(String keyFlagCd)
    {
        this.keyFlagCd = keyFlagCd;
    }

    public Integer getFldLengthNbr()
    {
        return fldLengthNbr;
    }

    public void setFldLengthNbr(Integer fldLengthNbr)
    {
        this.fldLengthNbr = fldLengthNbr;
    }

    public String getCNm()
    {
        return cNm;
    }

    public void setCNm(String cNm)
    {
        this.cNm = cNm;
    }

    public String getCobolNm()
    {
        return cobolNm;
    }

    public void setCobolNm(String cobolNm)
    {
        this.cobolNm = cobolNm;
    }

    public String getReqFlagCd()
    {
        return reqFlagCd;
    }

    public void setReqFlagCd(String reqFlagCd)
    {
        this.reqFlagCd = reqFlagCd;
    }

    public String getMoneyFlagCd()
    {
        return moneyFlagCd;
    }

    public void setMoneyFlagCd(String moneyFlagCd)
    {
        this.moneyFlagCd = moneyFlagCd;
    }

    public Integer getDecimalsNbr()
    {
        return decimalsNbr;
    }

    public void setDecimalsNbr(Integer decimalsNbr)
    {
        this.decimalsNbr = decimalsNbr;
    }

    public String getRangeFlagCd()
    {
        return rangeFlagCd;
    }

    public void setRangeFlagCd(String rangeFlagCd)
    {
        this.rangeFlagCd = rangeFlagCd;
    }

    public String getRangeLowCd()
    {
        return rangeLowCd;
    }

    public void setRangeLowCd(String rangeLowCd)
    {
        this.rangeLowCd = rangeLowCd;
    }

    public String getRangeHighCd()
    {
        return rangeHighCd;
    }

    public void setRangeHighCd(String rangeHighCd)
    {
        this.rangeHighCd = rangeHighCd;
    }

    public String getTableValFlagCd()
    {
        return tableValFlagCd;
    }

    public void setTableValFlagCd(String tableValFlagCd)
    {
        this.tableValFlagCd = tableValFlagCd;
    }

    public Integer getTableValFileNbr()
    {
        return tableValFileNbr;
    }

    public void setTableValFileNbr(Integer tableValFileNbr)
    {
        this.tableValFileNbr = tableValFileNbr;
    }

    public String getDateFlagCd()
    {
        return dateFlagCd;
    }

    public void setDateFlagCd(String dateFlagCd)
    {
        this.dateFlagCd = dateFlagCd;
    }

    public String getSecFieldFlagCd()
    {
        return secFieldFlagCd;
    }

    public void setSecFieldFlagCd(String secFieldFlagCd)
    {
        this.secFieldFlagCd = secFieldFlagCd;
    }

    public String getAlphaFlagCd()
    {
        return alphaFlagCd;
    }

    public void setAlphaFlagCd(String alphaFlagCd)
    {
        this.alphaFlagCd = alphaFlagCd;
    }

    public String getUpperCaseFlagCd()
    {
        return upperCaseFlagCd;
    }

    public void setUpperCaseFlagCd(String upperCaseFlagCd)
    {
        this.upperCaseFlagCd = upperCaseFlagCd;
    }

    public String getDerivedFlagCd()
    {
        return derivedFlagCd;
    }

    public void setDerivedFlagCd(String derivedFlagCd)
    {
        this.derivedFlagCd = derivedFlagCd;
    }

    public String getDefaultValueCd()
    {
        return defaultValueCd;
    }

    public void setDefaultValueCd(String defaultValueCd)
    {
        this.defaultValueCd = defaultValueCd;
    }

    public String getLongAliasNm()
    {
        return longAliasNm;
    }

    public void setLongAliasNm(String longAliasNm)
    {
        this.longAliasNm = longAliasNm;
    }

    public GenTable getGenTable()
    {
        return genTable;
    }

    public void setGenTable(GenTable genTable)
    {
        this.genTable = genTable;
    }

    /**
     * Get human-readable data type name from single-char code.
     */
    public String getDataTypeDisplay()
    {
        if (dataTypeNm == null) return "Unknown";
        switch (dataTypeNm)
        {
            case "C": return "Character";
            case "S": return "SmallInt";
            case "I": return "Integer";
            case "D": return "Decimal";
            case "F": return "Float";
            case "T": return "Timestamp";
            case "A": return "Date";
            case "B": return "Binary";
            default: return dataTypeNm;
        }
    }

    /**
     * Get display length (with decimals if applicable).
     */
    public String getLengthDisplay()
    {
        if (fldLengthNbr == null || fldLengthNbr == 0)
        {
            return "0";
        }
        if (decimalsNbr != null && decimalsNbr > 0)
        {
            return fldLengthNbr + "." + decimalsNbr;
        }
        return String.valueOf(fldLengthNbr);
    }

    /**
     * Get flags display (combines key, required, etc.).
     */
    public String getFlagsDisplay()
    {
        StringBuilder flags = new StringBuilder();
        if ("Y".equals(keyFlagCd)) flags.append("Key ");
        if ("Y".equals(reqFlagCd)) flags.append("Required ");
        if ("Y".equals(moneyFlagCd)) flags.append("Money ");
        if ("Y".equals(dateFlagCd)) flags.append("Date ");
        return flags.toString().trim();
    }

    @Override
    public String toString()
    {
        return columnNm;
    }
}
