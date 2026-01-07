package genowa.generator;

import genowa.util.IString;
import genowa.generator.trigger.TriggerRegistry;
import genowa.generator.genobj.AbstractGenerationObject;
import genowa.core.ProcessType;

/**
 * Abstract base class for all code generators.
 * Matches C++ AbstractGenerator class.
 */
public abstract class AbstractGenerator
{
    protected DataAccess dataAccess;
    protected GenerationControl genCtl;
    protected ProcessType procType;
    protected TriggerRegistry triggerRegistry;
    protected AbstractGenerationObject genObj;
    
    // State flags
    private boolean mainTpl;
    
    public AbstractGenerator()
    {
        this.procType = ProcessType.RATING;
        this.triggerRegistry = new TriggerRegistry();
    }
    
    public AbstractGenerator(DataAccess dataAccess)
    {
        this();
        this.dataAccess = dataAccess;
    }
    
    /**
     * Main entry point for generation.
     * Called from outside with InFile and OutFile already created.
     */
    public void genMain(String insLine, InFile ifl, OutFile ofl, boolean bMainTpl)
    {
        GenerationControl gCtl = new GenerationControl();
        if (triggerRegistry != null)
        {
            gCtl.setTriggerRegistry(triggerRegistry);
        }
        generate(gCtl, insLine, ifl, ofl, bMainTpl);
    }
    
    /**
     * Sub-template generation entry point.
     */
    public void genSub(GenCore genCore, InFile ifl, boolean bMainTpl)
    {
        GenerationControl gCtl = new GenerationControl();
        if (triggerRegistry != null)
        {
            gCtl.setTriggerRegistry(triggerRegistry);
        }
        if (!bMainTpl)
        {
            gCtl.setParentGenCore(genCore);
        }
        
        generate(gCtl, genCore.getInsLineCd().str(), ifl, genCore.getOutFile(), bMainTpl);
    }
    
    /**
     * Core generation method.
     */
    protected void generate(GenerationControl gCtl, String insLine,
                           InFile ifl, OutFile ofl, boolean bMainTpl)
    {
        this.setCoreData(gCtl, insLine, ifl, ofl, bMainTpl);
        this.doCollections();
        this.generate();
    }
    
    /**
     * Trigger the actual generation via GenerationControl.
     */
    protected void generate()
    {
        if (genCtl != null)
        {
            genCtl.generate(getGenCore());
        }
    }
    
    /**
     * Load data collections before generation.
     */
    protected void doCollections()
    {
        if (isMainTpl())
        {
            this.doMainCollections();
        }
        else
        {
            this.doSubCollections();
        }
    }
    
    /**
     * Hook for derived classes to load main template data.
     */
    protected void doMainCollections()
    {
        System.out.println("[DEBUG] Setting up main collections");
        
        GenCore genCore = getGenCore();
        if (genCore != null)
        {
            genCore.loadBusinessData();
        }
    }
    
    /**
     * Hook for derived classes to handle sub-template data.
     */
    protected void doSubCollections()
    {
        System.out.println("[DEBUG] Setting up sub collections");
    }
    
    /**
     * Generate using just insurance line (gets files from derived class).
     */
    public void genMainObject(String insLine)
    {
        String outFile = getOutFileName();
        this.genMainObject(insLine, outFile);
    }
    
    /**
     * Generate with insurance line and output file.
     */
    public void genMainObject(String insLineCd, String outFile)
    {
        String infile = getInFileName();
        this.genObject(insLineCd, infile, outFile);
    }
    
    /**
     * Core object generation - builds file objects and starts generation.
     */
    public void genObject(String insLine, String infile, String outfile)
    {
        InFile ifl = this.buildInFile(infile);
        OutFile ofl = this.buildOutFile(outfile);
        this.genMain(insLine, ifl, ofl, true);
    }
    
    /**
     * Set up core generation data.
     */
    protected void setCoreData(GenerationControl gCtl, String insLine,
                               InFile ifl, OutFile ofl, boolean bMainTpl)
    {
        System.out.println("[DEBUG] AbstractGenerator.setCoreData() called");
        
        this.setGenCtl(gCtl);
        this.setInsLine(insLine);
        this.setInFile(ifl);
        this.setOutFile(ofl);
        this.setCTLProcType(this.getProcType());
        this.setMainTpl(bMainTpl);
        
        // Connect the GenerationControl to template and output files
        if (gCtl != null)
        {
            gCtl.load(ifl, ofl);
            
            GenCore genCore = new GenCore(this.dataAccess);
            genCore.setInsLineCd(new IString(insLine));
            
            // Ensure DataAccess is connected
            if (this.dataAccess != null && !this.dataAccess.isConnected())
            {
                try
                {
                    this.dataAccess.connect("");
                    System.out.println("[DEBUG] Database connected successfully");
                }
                catch (Exception e)
                {
                    System.out.println("[WARNING] Failed to connect to database: " + e.getMessage());
                }
            }
            
            gCtl.setGenCore(genCore);
        }
    }
    
    /**
     * Get the GenCore from GenerationControl.
     */
    public GenCore getGenCore()
    {
        return genCtl != null ? genCtl.getGenCore() : null;
    }
    
    // Setters that delegate to GenCore
    
    protected void setInsLine(String insLine)
    {
        if (getGenCore() != null)
        {
            getGenCore().setInsLineCd(new IString(insLine));
        }
    }
    
    protected void setMainTpl(boolean mainTpl)
    {
        this.mainTpl = mainTpl;
        if (getGenCore() != null)
        {
            getGenCore().setMainTpl(mainTpl);
        }
    }
    
    public boolean isMainTpl()
    {
        return genCtl != null ? genCtl.isMainTpl() : this.mainTpl;
    }
    
    protected void setOutFile(OutFile ofl)
    {
        if (genCtl != null)
        {
            genCtl.setOutFile(ofl);
        }
    }
    
    protected void setInFile(InFile ifl)
    {
        if (getGenCore() != null)
        {
            getGenCore().setInFile(ifl);
        }
    }
    
    public ProcessType getProcType()
    {
        return procType;
    }
    
    public void setProcType(ProcessType procType)
    {
        this.procType = procType;
    }
    
    protected void setCTLProcType(ProcessType procType)
    {
        if (getGenCore() != null)
        {
            getGenCore().setProcType(procType);
        }
    }
    
    protected void setGenCtl(GenerationControl gCtl)
    {
        this.genCtl = gCtl;
    }
    
    public GenerationControl getGenCtl()
    {
        return genCtl;
    }
    
    /**
     * Build InFile for a template.
     * Override in derived classes to provide language-specific trigger registry.
     */
    protected InFile buildInFile(String file)
    {
        String fullPath = getTemplateDir() + "/" + file;
        System.out.println("[DEBUG] AbstractGenerator opening template file: " + fullPath);
        return new InFile(new IString(fullPath), triggerRegistry);
    }
    
    /**
     * Build OutFile for output.
     */
    protected OutFile buildOutFile(String file)
    {
        String fullPath = getGenPath() + "/" + file;
        System.out.println("[DEBUG] Creating output file: " + fullPath);
        return new OutFile(new IString(fullPath));
    }
    
    // Abstract methods for derived classes
    
    /**
     * Get template directory.
     */
    public String getTemplateDir()
    {
        return "template";
    }
    
    /**
     * Get output directory.
     */
    public String getGenPath()
    {
        return "generated";
    }
    
    /**
     * Get input template filename.
     */
    public abstract String getInFileName();
    
    /**
     * Get output filename.
     */
    public abstract String getOutFileName();
    
    // Trigger registry
    
    public TriggerRegistry getTriggerRegistry()
    {
        return triggerRegistry;
    }
    
    public void setTriggerRegistry(TriggerRegistry triggerRegistry)
    {
        this.triggerRegistry = triggerRegistry;
    }
    
    // Generation object handling
    public void setGenObj(AbstractGenerationObject obj)
    {
        this.genObj = obj;
    }
    
    public AbstractGenerationObject getGenObj()
    {
        return genObj;
    }
}
