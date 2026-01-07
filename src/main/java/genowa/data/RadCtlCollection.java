package genowa.data;

import genowa.generator.DataAccess;
import genowa.model.RadCtlItem;
import genowa.util.IString;
import genowa.util.StringUtils;
import java.util.ArrayList;
import java.util.List;

public class RadCtlCollection
{
    private DataAccess dataAccess;
    private List<RadCtlItem> primaryCtlItems;
    private List<RadCtlItem> premiumCtlItems;
    private List<RadCtlItem> formsCtlItems;
    private List<RadCtlItem> additionalCtlItems;
    private List<RadCtlItem> ctlList;
    private List<IString> ctlTables;
    private List<IString> covCdTbls;

    public RadCtlCollection()
    {
        this.primaryCtlItems = new ArrayList<>();
        this.premiumCtlItems = new ArrayList<>();
        this.formsCtlItems = new ArrayList<>();
        this.additionalCtlItems = new ArrayList<>();
        this.ctlList = new ArrayList<>();
        this.ctlTables = new ArrayList<>();
        this.covCdTbls = new ArrayList<>();
    }

    public RadCtlCollection(DataAccess dataAccess)
    {
        this();
        this.dataAccess = dataAccess;
    }

    // Data collection methods
    public void loadData(IString insuranceLine)
    {
        if (dataAccess == null)
        {
            System.err.println("[ERROR] No data access available for RadCtlCollection");
            return;
        }

        System.out.println("[DEBUG] Loading RadCtl data for insurance line: " + (insuranceLine != null ? insuranceLine.str() : ""));

        // Clear all data before loading to ensure a clean state
        clearAllData();

        // Load data from data access layer
        List<RadCtlItem> items = dataAccess.getRadCtlData(insuranceLine != null ? insuranceLine.str() : "");

        // Add all items to the main list
        for (RadCtlItem item : items)
        {
            addItem(item);
        }

        // Categorize all items at once after loading is complete
        categorizeItems();

        System.out.println("[DEBUG] Loaded " + ctlList.size() + " RadCtl items");
    }

    public void loadDataByTable(IString insuranceLine, IString tableName)
    {
        if (dataAccess == null)
        {
            System.err.println("[ERROR] No data access available for RadCtlCollection");
            return;
        }

        System.out.println("[DEBUG] Loading RadCtl data for insurance line: " + (insuranceLine != null ? insuranceLine.str() : "")
                + ", table: " + (tableName != null ? tableName.str() : ""));

        // Clear all data before loading to ensure a clean state
        clearAllData();

        // Load data from data access layer
        List<RadCtlItem> items = dataAccess.getRadCtlDataByTable(
            insuranceLine != null ? insuranceLine.str() : "",
            tableName != null ? tableName.str() : "");

        // Add all items to the main list
        for (RadCtlItem item : items)
        {
            addItem(item);
        }

        // Categorize all items at once after loading is complete
        categorizeItems();

        System.out.println("[DEBUG] Loaded " + ctlList.size() + " RadCtl items for table " + (tableName != null ? tableName.str() : ""));
    }

    // Item getters
    public RadCtlItem getPrimaryItem()
    {
        if (primaryCtlItems.isEmpty())
        {
            return null;
        }
        return primaryCtlItems.get(0);
    }

    public RadCtlItem getPremiumItem()
    {
        if (premiumCtlItems.isEmpty())
        {
            return null;
        }
        return premiumCtlItems.get(0);
    }

    public RadCtlItem getFormsItem()
    {
        if (formsCtlItems.isEmpty())
        {
            return null;
        }
        return formsCtlItems.get(0);
    }

    public RadCtlItem getAdditionalItem()
    {
        if (additionalCtlItems.isEmpty())
        {
            return null;
        }
        return additionalCtlItems.get(0);
    }

    // Table name getters
    public IString getPrimaryTbl()
    {
        RadCtlItem item = getPrimaryItem();
        return item != null && item.getTableNm() != null ? item.getTableNm() : new IString("UnknownTable");
    }

    public IString getPrimaryTblAsObject()
    {
        RadCtlItem item = getPrimaryItem();
        return item != null && item.getTableNm() != null ? convertToCamel(item.getTableNm(), true) : new IString("UnknownTable");
    }

    public IString getPremiumTbl()
    {
        RadCtlItem item = getPremiumItem();
        return item != null && item.getTableNm() != null ? item.getTableNm() : new IString("UnknownTable");
    }

    public IString getPremiumTblAsObject()
    {
        RadCtlItem item = getPremiumItem();
        return item != null && item.getTableNm() != null ? convertToCamel(item.getTableNm(), true) : new IString("UnknownTable");
    }

    public IString getFormsTbl()
    {
        RadCtlItem item = getFormsItem();
        return item != null && item.getTableNm() != null ? item.getTableNm() : new IString("UnknownTable");
    }

    public IString getFormsTblAsObject()
    {
        RadCtlItem item = getFormsItem();
        return item != null && item.getTableNm() != null ? convertToCamel(item.getTableNm(), true) : new IString("UnknownTable");
    }

    // Collection access
    public List<RadCtlItem> getPrimaryCtlItems()
    {
        return new ArrayList<>(primaryCtlItems);
    }

    public List<RadCtlItem> getPremiumCtlItems()
    {
        return new ArrayList<>(premiumCtlItems);
    }

    public List<RadCtlItem> getFormsCtlItems()
    {
        return new ArrayList<>(formsCtlItems);
    }

    public List<RadCtlItem> getAdditionalCtlItems()
    {
        return new ArrayList<>(additionalCtlItems);
    }

    public List<RadCtlItem> getCtlList()
    {
        return new ArrayList<>(ctlList);
    }

    // String conversion methods
    public IString convertToCamel(IString str, boolean removeV)
    {
        IString result = StringUtils.stripView(str);
        if (removeV && result != null && result.endsWith(new IString("_V")))
        {
            result = result.substr(0, result.length() - 2);
        }
        return StringUtils.convertToCamelCase(result);
    }

    public IString convertToCamel(IString str)
    {
        return convertToCamel(str, false);
    }

    public IString convertToGetter(IString str)
    {
        return StringUtils.convertToGetter(str);
    }

    public IString convertToSetter(IString str)
    {
        return StringUtils.convertToSetter(str);
    }

    // Utility methods
    public boolean hasCoverageCd(IString tableNm)
    {
        if (tableNm == null)
        {
            return false;
        }
        for (IString table : covCdTbls)
        {
            if (table != null && table.equals(tableNm))
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasCovSeqNbr(IString tableNm)
    {
        // Check if any item for this table has a coverage sequence number
        if (tableNm == null)
        {
            return false;
        }
        for (RadCtlItem item : ctlList)
        {
            IString itemTableNm = item.getTableNm();
            IString columnNm = item.getColumnNm();
            if (itemTableNm != null && itemTableNm.equals(tableNm) && 
                columnNm != null && columnNm.contains(new IString("COV_SEQ")))
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasCovEffDt(IString tableNm)
    {
        // Check if any item for this table has a coverage effective date
        if (tableNm == null)
        {
            return false;
        }
        for (RadCtlItem item : ctlList)
        {
            IString itemTableNm = item.getTableNm();
            IString columnNm = item.getColumnNm();
            if (itemTableNm != null && itemTableNm.equals(tableNm) && 
                columnNm != null && columnNm.contains(new IString("COV_EFF")))
            {
                return true;
            }
        }
        return false;
    }

    // Item management
    public void addItem(RadCtlItem item)
    {
        if (item == null)
        {
            return;
        }

        // Add the item to the master list
        ctlList.add(item);

        // Add table name to the unique tables list if not already present
        IString tableName = item.getTableNm();
        if (tableName != null)
        {
            boolean found = false;
            for (IString existing : ctlTables)
            {
                if (existing != null && existing.equals(tableName))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                ctlTables.add(tableName);
            }
        }

        // Add to coverage code tables if applicable and not already present
        IString ctlCode = item.getCtlCode();
        if (ctlCode != null && ctlCode.equals(new IString("COVERAGE")))
        {
            if (tableName != null)
            {
                boolean found = false;
                for (IString existing : covCdTbls)
                {
                    if (existing != null && existing.equals(tableName))
                    {
                        found = true;
                        break;
                    }
                }
                if (!found)
                {
                    covCdTbls.add(tableName);
                }
            }
        }
    }

    private void categorizeItems()
    {
        // This method is now the single point of categorization.
        // It assumes the category lists are already clear from clearAllData().
        for (RadCtlItem item : ctlList)
        {
            if (item.isPrimaryType())
            {
                primaryCtlItems.add(item);
            }
            else if (item.isPremiumType())
            {
                premiumCtlItems.add(item);
            }
            else if (item.isFormsType())
            {
                formsCtlItems.add(item);
            }
            else if (item.isAdditionalType())
            {
                additionalCtlItems.add(item);
            }
        }

        System.out.println("[DEBUG] Categorized items - Primary: " + primaryCtlItems.size()
                + ", Premium: " + premiumCtlItems.size()
                + ", Forms: " + formsCtlItems.size()
                + ", Additional: " + additionalCtlItems.size());
    }

    private void clearAllData()
    {
        primaryCtlItems.clear();
        premiumCtlItems.clear();
        formsCtlItems.clear();
        additionalCtlItems.clear();
        ctlList.clear();
        ctlTables.clear();
        covCdTbls.clear();
    }
}

