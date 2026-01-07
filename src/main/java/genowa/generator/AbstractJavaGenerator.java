package genowa.generator;

import genowa.util.IString;
import genowa.generator.trigger.TriggerRegistry;
import genowa.core.ProcessType;

/**
 * Abstract base class for Java code generators.
 * Matches C++ AbstractJavaGenerator class.
 */
public abstract class AbstractJavaGenerator extends AbstractGenerator
{
    public AbstractJavaGenerator()
    {
        super();
    }
    
    public AbstractJavaGenerator(DataAccess dataAccess)
    {
        super(dataAccess);
    }
    
    /**
     * Main generation method following Java pattern.
     */
    public void genMainObject(IString insLine)
    {
        // Default stub implementation
        System.out.println("[DEBUG] AbstractJavaGenerator::genMainObject called with " + insLine.str());
    }
    
    @Override
    public String getInFileName()
    {
        // Get template name from generation object (following Java pattern)
        if (genObj != null)
        {
            return genObj.getMainTemplateName().str();
        }
        // Fallback: provide a default Java template name
        return "JavaTest.tpl";
    }
    
    @Override
    public String getOutFileName()
    {
        // Get output file name from generation object (following Java pattern)
        if (genObj != null)
        {
            return genObj.getGenFileName().str();
        }
        // Fallback: use insurance line code from GenCore if available
        GenCore genCore = getGenCore();
        if (genCore != null && genCore.getInsLineCd() != null)
        {
            return "Java" + genCore.getInsLineCd().toUpper().str() + "RatingDriver.java";
        }
        return "JavaRatingDriver.java";
    }
    
    @Override
    public ProcessType getProcType()
    {
        return ProcessType.RATING; // Default for Java generators
    }
    
    @Override
    public String getTemplateDir()
    {
        return "template/java";
    }
    
    @Override
    protected void doMainCollections()
    {
        System.out.println("[DEBUG] Setting up Java main collections");
        // TODO: Implement Java-specific collection setup
        super.doMainCollections();
    }
    
    @Override
    protected void doSubCollections()
    {
        System.out.println("[DEBUG] Setting up Java sub collections");
        // TODO: Implement Java-specific sub collection setup
        super.doSubCollections();
    }
    
    /**
     * Build InFile with Java-specific trigger registry.
     * Note: Java trigger registry not yet implemented, using base registry for now.
     */
    @Override
    protected InFile buildInFile(String file)
    {
        // Override the base method to provide the Java-specific Trigger registry.
        String fullPath = getTemplateDir() + "/" + file;
        System.out.println("[DEBUG] AbstractJavaGenerator opening template file with JAVA trigger registry: " + fullPath);
        
        // TODO: Create JavaTriggerRegistry when implemented
        // For now, use the base trigger registry
        return new InFile(new IString(fullPath), triggerRegistry);
    }
}

