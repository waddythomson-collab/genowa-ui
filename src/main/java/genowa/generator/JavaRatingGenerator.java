package genowa.generator;

import genowa.util.IString;
import genowa.core.ProcessType;
import genowa.generator.genobj.AbstractGenerationObject;

/**
 * Java Rating code generator.
 * Matches C++ JavaRatingGenerator class.
 */
public class JavaRatingGenerator extends AbstractJavaGenerator
{
    public JavaRatingGenerator()
    {
        super();
    }
    
    public JavaRatingGenerator(DataAccess dataAccess)
    {
        super(dataAccess);
    }
    
    public JavaRatingGenerator(AbstractGenerationObject genObj)
    {
        super();
        this.genObj = genObj;
    }
    
    @Override
    public String getInFileName()
    {
        if (genObj != null)
        {
            return genObj.getMainTemplateName().str();
        }
        return super.getInFileName();
    }
    
    @Override
    public String getOutFileName()
    {
        if (genObj != null)
        {
            return genObj.getGenFileName().str();
        }
        return super.getOutFileName();
    }
    
    @Override
    public ProcessType getProcType()
    {
        return ProcessType.RATING;
    }
}

