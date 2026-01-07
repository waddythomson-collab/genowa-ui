package genowa.generator;

import genowa.util.IString;
import genowa.core.ProcessType;
import genowa.generator.genobj.AbstractGenerationObject;

/**
 * COBOL Rating code generator.
 * Matches C++ CobolRatingGenerator class.
 */
public class CobolRatingGenerator extends AbstractCobolGenerator
{
    public CobolRatingGenerator()
    {
        super();
    }
    
    public CobolRatingGenerator(DataAccess dataAccess)
    {
        super(dataAccess);
    }
    
    public CobolRatingGenerator(AbstractGenerationObject genObj)
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
