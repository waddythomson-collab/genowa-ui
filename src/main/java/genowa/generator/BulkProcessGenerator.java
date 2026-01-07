package genowa.generator;

import genowa.core.ProcessType;
import genowa.util.IString;

/**
 * Generator for bulk processes (IO, Issuance, Renewal).
 * All bulk processes use gen_tables and gen_fields as the foundation.
 * Matches the pattern where bulk processes were previously standalone applications.
 */
public class BulkProcessGenerator extends AbstractCobolGenerator
{
    private ProcessType bulkProcessType;
    
    public BulkProcessGenerator()
    {
        super();
        this.bulkProcessType = ProcessType.IO;
    }
    
    public BulkProcessGenerator(DataAccess dataAccess)
    {
        super(dataAccess);
        this.bulkProcessType = ProcessType.IO;
    }
    
    public BulkProcessGenerator(DataAccess dataAccess, ProcessType processType)
    {
        super(dataAccess);
        this.bulkProcessType = processType;
        this.procType = processType;
    }
    
    @Override
    public String getInFileName()
    {
        // Template name based on process type
        switch (bulkProcessType)
        {
            case IO:
                return "io_process.tpl";
            case ISSUANCE:
                return "issuance_process.tpl";
            case RENEWAL:
                return "renewal_process.tpl";
            default:
                return "bulk_process.tpl";
        }
    }
    
    @Override
    public String getOutFileName()
    {
        // Output file name based on process type and insurance line
        GenCore genCore = getGenCore();
        String insLine = genCore != null ? genCore.getInsLineCd().str() : "GEN";
        
        switch (bulkProcessType)
        {
            case IO:
                return insLine + "IO.cbl";
            case ISSUANCE:
                return insLine + "ISSUE.cbl";
            case RENEWAL:
                return insLine + "RENEW.cbl";
            default:
                return insLine + "BULK.cbl";
        }
    }
    
    @Override
    public ProcessType getProcType()
    {
        return bulkProcessType;
    }
    
    @Override
    public String getTemplateDir()
    {
        return "template/cobol/bulk";
    }
    
    @Override
    protected void doMainCollections()
    {
        System.out.println("[DEBUG] Setting up bulk process collections for: " + bulkProcessType);
        
        // Bulk processes rely on gen_tables and gen_fields loaded in GenCore.loadBusinessData()
        // Additional process-specific configuration can be loaded here
        GenCore genCore = getGenCore();
        if (genCore != null && genCore.getDataAccess() instanceof JpaDataAccess)
        {
            JpaDataAccess jpaAccess = (JpaDataAccess) genCore.getDataAccess();
            
            // Load rules specific to this bulk process type
            jpaAccess.loadGenRules();
            
            // Load templates for this process type
            jpaAccess.loadGenTemplates();
            
            System.out.println("[DEBUG] Bulk process configuration loaded");
        }
        
        super.doMainCollections();
    }
    
    /**
     * Generate code for a specific table using gen_tables/gen_fields.
     */
    public void generateForTable(String tableName)
    {
        GenCore genCore = getGenCore();
        if (genCore == null || genCore.getDataAccess() == null)
        {
            System.err.println("[ERROR] Cannot generate - missing GenCore or DataAccess");
            return;
        }
        
        System.out.println("[DEBUG] Generating " + bulkProcessType + " code for table: " + tableName);
        
        // The template will use triggers that query gen_tables/gen_fields
        // to generate the appropriate code for the table
        String insLine = genCore.getInsLineCd().str();
        String template = getInFileName();
        String output = getOutFileName();
        
        genObject(insLine, template, output);
    }
}

