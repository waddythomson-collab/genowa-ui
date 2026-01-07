package genowa.generator.genobj;

import genowa.util.IString;

/**
 * Abstract base class for generation objects.
 * Matches C++ AbstractGenerationObject class.
 */
public abstract class AbstractGenerationObject
{
    protected IString name;
    protected IString insLineCd;

    public AbstractGenerationObject()
    {
        this.name = new IString("");
        this.insLineCd = new IString("");
    }

    public AbstractGenerationObject(IString insLineCd)
    {
        this();
        this.insLineCd = insLineCd != null ? insLineCd : new IString("");
    }

    // Core methods that subclasses must implement
    public abstract IString getMainTemplateName();
    public abstract void setGenFileName(IString name);

    public IString getGenFileName()
    {
        return name;
    }

    // Insurance line handling
    public IString getInsLineCd()
    {
        return insLineCd;
    }

    public void setInsLineCd(IString insLineCd)
    {
        this.insLineCd = insLineCd != null ? insLineCd : new IString("");
    }

    // Name handling
    public IString getName()
    {
        return name;
    }

    public void setName(IString name)
    {
        this.name = name != null ? name : new IString("");
    }
}

