package genowa.generator.genobj;

import genowa.util.IString;

/**
 * Java rating driver generation object.
 * Matches C++ JavaRatingDriver class.
 */
public class JavaRatingDriver extends AbstractGenerationObject
{
    private IString genFileName;

    public JavaRatingDriver(IString insLine)
    {
        super(insLine);
        this.genFileName = new IString("");
    }

    @Override
    public IString getMainTemplateName()
    {
        // Return the main template name for Java rating
        return new IString("java_rating_main.tpl");
    }

    @Override
    public IString getGenFileName()
    {
        return genFileName != null ? genFileName : new IString("");
    }

    @Override
    public void setGenFileName(IString name)
    {
        this.genFileName = name != null ? name : new IString("");
    }
}

