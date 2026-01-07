package genowa.generator;

import genowa.core.ProcessType;
import genowa.data.RadCtlCollection;
import genowa.util.IString;
import genowa.util.Line;
import genowa.util.Token;

/**
 * Core generation state holder.
 * Matches C++ GenCore class - holds all state during code generation.
 */
public class GenCore
{
    // File handling
    private InFile inFile;
    private OutFile outFile;

    // Line and token handling
    private Line line;
    private Token token;

    // Collection handling
    private RadCtlCollection ctlCollect;

    // Data access
    private DataAccess dataAccess;

    // Insurance line handling
    private IString insLineCd;

    // Template type handling
    private boolean bMainTpl = false;

    // Process type handling
    private ProcessType procType = ProcessType.RATING;

    public GenCore()
    {
        this.insLineCd = new IString("");
    }

    public GenCore(DataAccess dataAccess)
    {
        this();
        this.dataAccess = dataAccess;
        if (dataAccess != null)
        {
            this.ctlCollect = new RadCtlCollection(dataAccess);
        }
    }

    // File handling
    public InFile getInFile()
    {
        return inFile;
    }

    public void setInFile(InFile file)
    {
        this.inFile = file;
    }

    public OutFile getOutFile()
    {
        return outFile;
    }

    public void setOutFile(OutFile file)
    {
        this.outFile = file;
    }

    // Line and token handling
    public Line getLine()
    {
        return line;
    }

    public void setLine(Line newLine)
    {
        this.line = newLine;
    }

    public Token getToken()
    {
        return token;
    }

    public void setToken(Token newToken)
    {
        this.token = newToken;
    }

    // Collection handling
    public RadCtlCollection getCtlCollect()
    {
        return ctlCollect;
    }

    public void setCtlCollect(RadCtlCollection collection)
    {
        this.ctlCollect = collection;
    }

    // Data access handling
    public DataAccess getDataAccess()
    {
        return dataAccess;
    }

    public void setDataAccess(DataAccess access)
    {
        this.dataAccess = access;
    }

    // Insurance line handling
    public IString getInsLineCd()
    {
        return insLineCd;
    }

    public void setInsLineCd(IString code)
    {
        this.insLineCd = code != null ? code : new IString("");
    }

    // Template type handling
    public boolean isMainTpl()
    {
        return bMainTpl;
    }

    public void setMainTpl(boolean mainTpl)
    {
        this.bMainTpl = mainTpl;
    }

    // Process type handling
    public ProcessType getProcType()
    {
        return procType;
    }

    public void setProcType(ProcessType type)
    {
        this.procType = type;
    }

    // Token replacement
    public void replaceTokenWith(IString str)
    {
        if (outFile != null)
        {
            outFile.writeLine(str);
        }
    }

    // Data loading
    public void loadBusinessData()
    {
        if (dataAccess == null)
        {
            System.err.println("[ERROR] Cannot load business data - missing data access");
            return;
        }

        if (insLineCd == null || insLineCd.empty())
        {
            System.err.println("[ERROR] Cannot load business data - no insurance line code set");
            return;
        }

        System.out.println("[DEBUG] Loading business data for insurance line: " + insLineCd.str() + 
                          ", process type: " + procType);

        // Foundation: Always load gen_tables and gen_fields first
        // These provide the database schema image that all code generation works with
        System.out.println("[DEBUG] Loading gen_tables and gen_fields (foundation)...");
        if (dataAccess != null)
        {
            // Load gen_tables - the foundation for all code generation
            dataAccess.loadGenTables();
            // Load fields for all tables (or specific tables based on process type)
            // For now, we'll load fields on-demand when processing specific tables
        }

        // Load process-specific data
        if (procType == ProcessType.RATING || procType == ProcessType.EDITS || 
            procType == ProcessType.UNDERWRITING)
        {
            // Rating/Edits/Underwriting use RadCtlCollection for business logic
            if (ctlCollect != null)
            {
                ctlCollect.loadData(insLineCd);
            }
        }
        else if (procType == ProcessType.IO || procType == ProcessType.ISSUANCE || 
                 procType == ProcessType.RENEWAL)
        {
            // Bulk processes primarily use gen_tables/gen_fields
            // Additional configuration may be loaded from gen_rules or gen_templates
            System.out.println("[DEBUG] Loading bulk process configuration...");
            if (dataAccess instanceof JpaDataAccess)
            {
                JpaDataAccess jpaAccess = (JpaDataAccess) dataAccess;
                jpaAccess.loadGenRules();
                jpaAccess.loadGenTemplates();
            }
        }

        System.out.println("[DEBUG] Business data loaded successfully");
    }
}
