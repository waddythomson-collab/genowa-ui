package genowa.generator;

import genowa.core.ProcessType;
import genowa.generator.trigger.TriggerRegistry;
import genowa.generator.trigger.cobol.CobolTriggers;

/**
 * Abstract base class for COBOL code generators.
 * Matches C++ AbstractCobolGenerator class.
 */
public abstract class AbstractCobolGenerator extends AbstractGenerator
{
    public AbstractCobolGenerator()
    {
        super();
        initializeCobolTriggers();
    }
    
    public AbstractCobolGenerator(DataAccess dataAccess)
    {
        super(dataAccess);
        initializeCobolTriggers();
    }
    
    /**
     * Initialize the COBOL-specific trigger registry.
     */
    private void initializeCobolTriggers()
    {
        CobolTriggers.registerAll(triggerRegistry);
    }
    
    @Override
    public String getInFileName()
    {
        // Default template name - override in derived classes
        return "template.tpl";
    }
    
    @Override
    public String getOutFileName()
    {
        // Default output name - override in derived classes
        return "output.cbl";
    }
    
    @Override
    public ProcessType getProcType()
    {
        return ProcessType.RATING;
    }
    
    @Override
    public String getTemplateDir()
    {
        return "template/cobol";
    }
    
    @Override
    protected void doMainCollections()
    {
        System.out.println("[DEBUG] Setting up COBOL main collections");
        super.doMainCollections();
    }
    
    @Override
    protected void doSubCollections()
    {
        System.out.println("[DEBUG] Setting up COBOL sub collections");
        super.doSubCollections();
    }
    
    /**
     * Build InFile with COBOL trigger registry.
     */
    @Override
    protected InFile buildInFile(String file)
    {
        String fullPath = getTemplateDir() + "/" + file;
        System.out.println("[DEBUG] AbstractCobolGenerator opening template file with COBOL trigger registry: " + fullPath);
        return new InFile(new genowa.util.IString(fullPath), triggerRegistry);
    }
}
