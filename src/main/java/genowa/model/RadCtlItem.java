package genowa.model;

import genowa.util.IString;

/**
 * RadCtlItem - represents a RAD control item.
 * Matches C++ RadCtlItem class, maintaining IString usage.
 */
public class RadCtlItem
{
    private IString tableNm;
    private IString typeCd;
    private IString insuranceLineCd;
    private int sequenceNbr;
    private int subLevelNbr;
    private IString columnNm;
    private IString ctlCode;
    private IString ioModuleNm;
    private IString linkagePrefix;

    public RadCtlItem()
    {
        this.sequenceNbr = 0;
        this.subLevelNbr = 0;
    }

    // Getters and setters
    public IString getTableNm()
    {
        return tableNm;
    }

    public void setTableNm(IString name)
    {
        this.tableNm = name;
    }

    public IString getTypeCd()
    {
        return typeCd;
    }

    public void setTypeCd(IString code)
    {
        this.typeCd = code;
    }

    public IString getInsuranceLineCd()
    {
        return insuranceLineCd;
    }

    public void setInsuranceLineCd(IString code)
    {
        this.insuranceLineCd = code;
    }

    public int getSequenceNbr()
    {
        return sequenceNbr;
    }

    public void setSequenceNbr(int nbr)
    {
        this.sequenceNbr = nbr;
    }

    public int getSubLevelNbr()
    {
        return subLevelNbr;
    }

    public void setSubLevelNbr(int nbr)
    {
        this.subLevelNbr = nbr;
    }

    public IString getColumnNm()
    {
        return columnNm;
    }

    public void setColumnNm(IString name)
    {
        this.columnNm = name;
    }

    public IString getCtlCode()
    {
        return ctlCode;
    }

    public void setCtlCode(IString code)
    {
        this.ctlCode = code;
    }

    public IString getIoModuleNm()
    {
        return ioModuleNm;
    }

    public void setIoModuleNm(IString name)
    {
        this.ioModuleNm = name;
    }

    public IString getLinkagePrefix()
    {
        return linkagePrefix;
    }

    public void setLinkagePrefix(IString prefix)
    {
        this.linkagePrefix = prefix;
    }

    // Utility methods
    public boolean isPrimaryType()
    {
        return typeCd != null && typeCd.length() > 0 && typeCd.charAt(0) == 'P';
    }

    public boolean isPremiumType()
    {
        return typeCd != null && typeCd.length() > 0 && typeCd.charAt(0) == '$';
    }

    public boolean isAdditionalType()
    {
        return typeCd != null && typeCd.length() > 0 && typeCd.charAt(0) == 'C';
    }

    public boolean isFormsType()
    {
        return typeCd != null && typeCd.length() > 0 && typeCd.charAt(0) == 'F';
    }

    // Check if this item is accessed via direct SQL rather than a standard I/O module
    public boolean isDirectSqlAccess()
    {
        if (ctlCode == null)
        {
            return false;
        }
        IString min = new IString("MIN");
        IString max = new IString("MAX");
        IString sum = new IString("SUM");
        IString count = new IString("COUNT");
        IString fetch = new IString("FETCH");
        IString read = new IString("READ");
        
        return ctlCode.equals(min) ||
               ctlCode.equals(max) ||
               ctlCode.equals(sum) ||
               ctlCode.equals(count) ||
               ctlCode.equals(fetch) ||
               ctlCode.equals(read);
    }

    // String representation
    public IString toIString()
    {
        IString result = new IString("RadCtlItem{");
        result = result.add(new IString("tableNm='")).add(tableNm != null ? tableNm : new IString("")).add(new IString("', "));
        result = result.add(new IString("typeCd='")).add(typeCd != null ? typeCd : new IString("")).add(new IString("', "));
        result = result.add(new IString("insuranceLineCd='")).add(insuranceLineCd != null ? insuranceLineCd : new IString("")).add(new IString("', "));
        result = result.add(new IString("sequenceNbr=")).add(new IString(sequenceNbr)).add(new IString(", "));
        result = result.add(new IString("subLevelNbr=")).add(new IString(subLevelNbr)).add(new IString(", "));
        result = result.add(new IString("columnNm='")).add(columnNm != null ? columnNm : new IString("")).add(new IString("', "));
        result = result.add(new IString("ctlCode='")).add(ctlCode != null ? ctlCode : new IString("")).add(new IString("', "));
        result = result.add(new IString("ioModuleNm='")).add(ioModuleNm != null ? ioModuleNm : new IString("")).add(new IString("', "));
        result = result.add(new IString("linkagePrefix='")).add(linkagePrefix != null ? linkagePrefix : new IString("")).add(new IString("'}"));
        return result;
    }
}
