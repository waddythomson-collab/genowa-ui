package genowa.algorithm.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a complete algorithm module.
 * Contains metadata from WARP_ALGORITHM_MST and steps from WARP_ALGORITHM.
 */
public class AlgorithmModule
{
    // Module identification
    private String moduleName;           // WARP_MODULE_NM (8 chars)
    private LocalDate effectiveDate;     // EFFECTIVE_DT
    private LocalDate expirationDate;    // EXPIRATION_DT (nullable)
    
    // Module metadata (from WARP_ALGORITHM_MST)
    private String jurisdictionCode;     // WARP_JUR_CD
    private String insuranceLineCode;    // WARP_INS_LINE_CD
    private String coverageCode;         // WARP_COV_CD
    private String midtermIndicator;     // WARP_MDTRM_IND
    private String rateLevelCode;        // WARP_RATE_LVL_CD
    
    // Description/documentation
    private String description;          // User-friendly description
    private String comments;             // Additional notes
    
    // Active flag (explicit, overrides date-based logic if set)
    private Boolean active;              // null = use date logic, true/false = explicit
    
    // Algorithm steps
    private List<AlgorithmStep> steps = new ArrayList<>();
    
    // Default constructor for YAML
    public AlgorithmModule()
    {
    }
    
    // Constructor with key fields
    public AlgorithmModule(String moduleName, LocalDate effectiveDate)
    {
        this.moduleName = moduleName;
        this.effectiveDate = effectiveDate;
    }
    
    // Getters and Setters
    public String getModuleName()
    {
        return moduleName;
    }
    
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }
    
    public LocalDate getEffectiveDate()
    {
        return effectiveDate;
    }
    
    public void setEffectiveDate(LocalDate effectiveDate)
    {
        this.effectiveDate = effectiveDate;
    }
    
    public LocalDate getExpirationDate()
    {
        return expirationDate;
    }
    
    public void setExpirationDate(LocalDate expirationDate)
    {
        this.expirationDate = expirationDate;
    }
    
    public String getJurisdictionCode()
    {
        return jurisdictionCode;
    }
    
    public void setJurisdictionCode(String jurisdictionCode)
    {
        this.jurisdictionCode = jurisdictionCode;
    }
    
    public String getInsuranceLineCode()
    {
        return insuranceLineCode;
    }
    
    public void setInsuranceLineCode(String insuranceLineCode)
    {
        this.insuranceLineCode = insuranceLineCode;
    }
    
    public String getCoverageCode()
    {
        return coverageCode;
    }
    
    public void setCoverageCode(String coverageCode)
    {
        this.coverageCode = coverageCode;
    }
    
    public String getMidtermIndicator()
    {
        return midtermIndicator;
    }
    
    public void setMidtermIndicator(String midtermIndicator)
    {
        this.midtermIndicator = midtermIndicator;
    }
    
    public String getRateLevelCode()
    {
        return rateLevelCode;
    }
    
    public void setRateLevelCode(String rateLevelCode)
    {
        this.rateLevelCode = rateLevelCode;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getComments()
    {
        return comments;
    }
    
    public void setComments(String comments)
    {
        this.comments = comments;
    }
    
    public List<AlgorithmStep> getSteps()
    {
        return steps;
    }
    
    public void setSteps(List<AlgorithmStep> steps)
    {
        this.steps = steps;
    }
    
    // Convenience methods
    public void addStep(AlgorithmStep step)
    {
        steps.add(step);
    }
    
    public int getStepCount()
    {
        return steps.size();
    }
    
    /**
     * Generate the YAML filename for this module
     */
    public String getYamlFilename()
    {
        return effectiveDate.toString() + ".yaml";
    }
    
    /**
     * Generate the directory path for this module
     */
    public String getDirectoryPath()
    {
        // Organize by insurance line, then module name
        String line = insuranceLineCode != null ? insuranceLineCode : "UNKNOWN";
        return line + "/" + moduleName;
    }
    
    /**
     * Set explicit active flag.
     */
    public void setActive(boolean active)
    {
        this.active = active;
    }
    
    /**
     * Check if this algorithm is currently active.
     * Uses explicit flag if set, otherwise calculates from dates.
     */
    public boolean isActive()
    {
        // If explicit flag is set, use it
        if (active != null)
        {
            return active;
        }
        
        // Otherwise calculate from effective/expiration dates
        LocalDate today = LocalDate.now();
        boolean afterEffective = !today.isBefore(effectiveDate);
        boolean beforeExpiration = expirationDate == null || !today.isAfter(expirationDate);
        return afterEffective && beforeExpiration;
    }
    
    @Override
    public String toString()
    {
        return String.format("Algorithm %s (eff: %s) - %d steps", 
            moduleName, effectiveDate, steps.size());
    }
}
