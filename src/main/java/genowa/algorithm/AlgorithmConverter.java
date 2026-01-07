package genowa.algorithm;

import genowa.algorithm.model.AlgorithmModule;
import genowa.algorithm.model.AlgorithmStep;
// TODO: Create GenAlgorithmRepository
// import genowa.repository.GenAlgorithmRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Converts algorithms from database (WARP_ALGORITHM tables) to YAML files.
 * 
 * Used for one-time migration of existing algorithms to the new YAML format.
 */
public class AlgorithmConverter
{
    // TODO: Create GenAlgorithmRepository
    // private final GenAlgorithmRepository repository;
    private final AlgorithmLoader loader;
    private Consumer<String> progressCallback;
    
    /**
     * Create converter with repository and output path.
     * 
     * @param repository database repository for reading algorithms
     * @param outputPath base path for YAML output
     */
    public AlgorithmConverter(/* GenAlgorithmRepository repository, */ Path outputPath)
    {
        // this.repository = repository;
        this.loader = new AlgorithmLoader(outputPath);
    }
    
    /**
     * Set callback for progress updates.
     * 
     * @param callback receives progress messages
     */
    public void setProgressCallback(Consumer<String> callback)
    {
        this.progressCallback = callback;
    }
    
    private void reportProgress(String message)
    {
        if (progressCallback != null)
        {
            progressCallback.accept(message);
        }
    }
    
    /**
     * Convert all algorithms for an insurance line.
     * 
     * @param insuranceLine insurance line code (e.g., "PA")
     * @return conversion result with counts and any errors
     */
    public ConversionResult convertByInsuranceLine(String insuranceLine)
    {
        ConversionResult result = new ConversionResult();
        
        reportProgress("Finding algorithms for insurance line: " + insuranceLine);
        
        // TODO: Implement repository
        // List<Object[]> modules = repository.findModulesByInsuranceLine(insuranceLine);
        List<Object[]> modules = new ArrayList<>(); // Stub - repository not implemented yet
        result.setTotalFound(modules.size());
        
        reportProgress("Found " + modules.size() + " algorithm modules");
        
        for (Object[] moduleRow : modules)
        {
            String moduleName = (String) moduleRow[0];
            LocalDate effectiveDate = (LocalDate) moduleRow[1];
            
            try
            {
                convertModule(insuranceLine, moduleName, effectiveDate);
                result.incrementConverted();
                reportProgress("Converted: " + moduleName + " (" + effectiveDate + ")");
            }
            catch (Exception e)
            {
                result.addError(moduleName, effectiveDate, e.getMessage());
                reportProgress("ERROR: " + moduleName + " - " + e.getMessage());
            }
        }
        
        return result;
    }
    
    /**
     * Convert a single algorithm module.
     * 
     * @param insuranceLine insurance line code
     * @param moduleName module name
     * @param effectiveDate effective date
     * @return the converted module
     * @throws IOException if save fails
     */
    public AlgorithmModule convertModule(
            String insuranceLine, 
            String moduleName, 
            LocalDate effectiveDate) throws IOException
    {
        // Load module metadata from WARP_ALGORITHM_MST
        AlgorithmModule module = loadModuleMetadata(
            insuranceLine, moduleName, effectiveDate);
        
        // Load steps from WARP_ALGORITHM
        List<AlgorithmStep> steps = loadSteps(moduleName, effectiveDate);
        module.setSteps(steps);
        
        // Save to YAML
        loader.save(module);
        
        return module;
    }
    
    /**
     * Load module metadata from WARP_ALGORITHM_MST.
     */
    private AlgorithmModule loadModuleMetadata(
            String insuranceLine, 
            String moduleName, 
            LocalDate effectiveDate)
    {
        // TODO: Implement repository
        // Object[] row = repository.findModuleMetadata(moduleName, effectiveDate);
        Object[] row = null; // Stub - repository not implemented yet
        
        AlgorithmModule module = new AlgorithmModule(moduleName, effectiveDate);
        module.setInsuranceLineCode(insuranceLine);
        
        if (row != null)
        {
            // Map fields from query result
            // Expected order: JUR_CD, COV_CD, MDTRM_IND, RATE_LVL_CD, EXPIRATION_DT
            module.setJurisdictionCode((String) row[0]);
            module.setCoverageCode((String) row[1]);
            module.setMidtermIndicator((String) row[2]);
            module.setRateLevelCode((String) row[3]);
            module.setExpirationDate((LocalDate) row[4]);
        }
        
        return module;
    }
    
    /**
     * Load algorithm steps from WARP_ALGORITHM.
     */
    private List<AlgorithmStep> loadSteps(String moduleName, LocalDate effectiveDate)
    {
        List<AlgorithmStep> steps = new ArrayList<>();
        
        // TODO: Implement repository
        // List<Object[]> rows = repository.findSteps(moduleName, effectiveDate);
        List<Object[]> rows = new ArrayList<>(); // Stub - repository not implemented yet
        
        for (Object[] row : rows)
        {
            // Expected order from query:
            // SEQ_NBR, OPERAND_CD, RT_ELEMENT_NM, VALUE_TYPE_CD, 
            // CON_IND, ROU_TYPE_CD, ROU_VALUE_AMT, COMMENT_TXT
            int sequence = ((Number) row[0]).intValue();
            String operandCd = (String) row[1];
            String element = (String) row[2];
            String valueTypeCd = (String) row[3];
            String conInd = (String) row[4];
            String roundTypeCd = (String) row[5];
            int roundValue = row[6] != null ? ((Number) row[6]).intValue() : 0;
            String comment = (String) row[7];
            
            // Check if element is a rate key or database element
            // TODO: Implement repository
            // boolean isRateKey = repository.isRateKey(element);
            // boolean isDatabaseElement = repository.isDatabaseElement(element);
            boolean isRateKey = false; // Stub - repository not implemented yet
            boolean isDatabaseElement = false; // Stub - repository not implemented yet
            
            AlgorithmStep step = AlgorithmStep.fromLegacy(
                sequence, operandCd, element, valueTypeCd, conInd,
                roundTypeCd, roundValue, comment, isRateKey, isDatabaseElement);
            
            steps.add(step);
        }
        
        return steps;
    }
    
    /**
     * Preview conversion without saving - useful for validation.
     * 
     * @param moduleName module name
     * @param effectiveDate effective date
     * @return the module that would be created
     */
    public AlgorithmModule preview(String moduleName, LocalDate effectiveDate)
    {
        // TODO: Implement repository
        // String insuranceLine = repository.getInsuranceLineForModule(moduleName);
        String insuranceLine = ""; // Stub - repository not implemented yet
        
        AlgorithmModule module = loadModuleMetadata(
            insuranceLine, moduleName, effectiveDate);
        
        List<AlgorithmStep> steps = loadSteps(moduleName, effectiveDate);
        module.setSteps(steps);
        
        return module;
    }
    
    /**
     * Result of a conversion operation.
     */
    public static class ConversionResult
    {
        private int totalFound;
        private int converted;
        private List<ConversionError> errors = new ArrayList<>();
        
        public int getTotalFound() { return totalFound; }
        public void setTotalFound(int totalFound) { this.totalFound = totalFound; }
        
        public int getConverted() { return converted; }
        public void incrementConverted() { this.converted++; }
        
        public List<ConversionError> getErrors() { return errors; }
        
        public void addError(String moduleName, LocalDate effectiveDate, String message)
        {
            errors.add(new ConversionError(moduleName, effectiveDate, message));
        }
        
        public boolean hasErrors() { return !errors.isEmpty(); }
        
        public int getErrorCount() { return errors.size(); }
        
        @Override
        public String toString()
        {
            return String.format("Conversion: %d/%d successful, %d errors",
                converted, totalFound, errors.size());
        }
    }
    
    /**
     * Details of a conversion error.
     */
    public static class ConversionError
    {
        private final String moduleName;
        private final LocalDate effectiveDate;
        private final String message;
        
        public ConversionError(String moduleName, LocalDate effectiveDate, String message)
        {
            this.moduleName = moduleName;
            this.effectiveDate = effectiveDate;
            this.message = message;
        }
        
        public String getModuleName() { return moduleName; }
        public LocalDate getEffectiveDate() { return effectiveDate; }
        public String getMessage() { return message; }
        
        @Override
        public String toString()
        {
            return moduleName + " (" + effectiveDate + "): " + message;
        }
    }
}
