package genowa.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity for gen_algorithm table.
 * Stores algorithm header/metadata information.
 */
@Entity
@Table(name = "gen_algorithm")
public class GenAlgorithm {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gen_algorithm_id")
    private Integer algorithmId;
    
    @Column(name = "gen_algorithm_nm", length = 50, nullable = false)
    private String algorithmName;
    
    @Column(name = "gen_algorithm_desc", length = 255)
    private String algorithmDescription;
    
    @Column(name = "gen_ins_line_cd", length = 10)
    private String insuranceLineCode;
    
    @Column(name = "gen_jur_cd", length = 10)
    private String jurisdictionCode;
    
    @Column(name = "gen_cov_cd", length = 10)
    private String coverageCode;
    
    @Column(name = "gen_mdtrm_ind", length = 1)
    private String midtermIndicator;
    
    @Column(name = "gen_rate_lvl_cd", length = 20)
    private String rateLevelCode;
    
    @Column(name = "effective_dt")
    private LocalDate effectiveDate;
    
    @Column(name = "expiration_dt")
    private LocalDate expirationDate;
    
    @Column(name = "comments", length = 1000)
    private String comments;
    
    @Column(name = "gen_algorithm_config", columnDefinition = "TEXT")
    private String algorithmConfig; // JSON configuration
    
    @OneToMany(mappedBy = "algorithm", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequence ASC")
    private List<GenAlgorithmStep> steps = new ArrayList<>();
    
    // Constructors
    public GenAlgorithm() {
    }
    
    public GenAlgorithm(String algorithmName, LocalDate effectiveDate) {
        this.algorithmName = algorithmName;
        this.effectiveDate = effectiveDate;
    }
    
    // Getters and Setters
    public Integer getAlgorithmId() {
        return algorithmId;
    }
    
    public void setAlgorithmId(Integer algorithmId) {
        this.algorithmId = algorithmId;
    }
    
    public String getAlgorithmName() {
        return algorithmName;
    }
    
    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }
    
    public String getAlgorithmDescription() {
        return algorithmDescription;
    }
    
    public void setAlgorithmDescription(String algorithmDescription) {
        this.algorithmDescription = algorithmDescription;
    }
    
    public String getInsuranceLineCode() {
        return insuranceLineCode;
    }
    
    public void setInsuranceLineCode(String insuranceLineCode) {
        this.insuranceLineCode = insuranceLineCode;
    }
    
    public String getJurisdictionCode() {
        return jurisdictionCode;
    }
    
    public void setJurisdictionCode(String jurisdictionCode) {
        this.jurisdictionCode = jurisdictionCode;
    }
    
    public String getCoverageCode() {
        return coverageCode;
    }
    
    public void setCoverageCode(String coverageCode) {
        this.coverageCode = coverageCode;
    }
    
    public String getMidtermIndicator() {
        return midtermIndicator;
    }
    
    public void setMidtermIndicator(String midtermIndicator) {
        this.midtermIndicator = midtermIndicator;
    }
    
    public String getRateLevelCode() {
        return rateLevelCode;
    }
    
    public void setRateLevelCode(String rateLevelCode) {
        this.rateLevelCode = rateLevelCode;
    }
    
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }
    
    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    public LocalDate getExpirationDate() {
        return expirationDate;
    }
    
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public String getAlgorithmConfig() {
        return algorithmConfig;
    }
    
    public void setAlgorithmConfig(String algorithmConfig) {
        this.algorithmConfig = algorithmConfig;
    }
    
    public List<GenAlgorithmStep> getSteps() {
        return steps;
    }
    
    public void setSteps(List<GenAlgorithmStep> steps) {
        this.steps = steps;
    }
    
    public void addStep(GenAlgorithmStep step) {
        steps.add(step);
        step.setAlgorithm(this);
    }
    
    public void removeStep(GenAlgorithmStep step) {
        steps.remove(step);
        step.setAlgorithm(null);
    }
}

