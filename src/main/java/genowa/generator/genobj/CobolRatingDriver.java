package genowa.generator.genobj;

import genowa.util.IString;

/**
 * COBOL rating driver generation object.
 * Matches C++ CobolRatingDriver class.
 */
public class CobolRatingDriver extends AbstractGenerationObject
{
    public CobolRatingDriver()
    {
        super();
    }

    public CobolRatingDriver(IString insLineCd)
    {
        super(insLineCd);
    }

    @Override
    public IString getMainTemplateName()
    {
        // Return the main template name for COBOL rating
        return new IString("cobol_rating_main.tpl");
    }

    @Override
    public void setGenFileName(IString name)
    {
        this.name = name != null ? name : new IString("");
    }
}

