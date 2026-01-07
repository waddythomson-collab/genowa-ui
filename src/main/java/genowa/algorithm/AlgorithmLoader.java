package genowa.algorithm;

import genowa.algorithm.model.AlgorithmModule;
import genowa.algorithm.model.AlgorithmStep;
import genowa.algorithm.model.Operator;
import genowa.algorithm.model.RoundingType;
import genowa.algorithm.model.ValueType;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Loads and saves AlgorithmModule objects from/to YAML files.
 * 
 * File naming convention: {MODULE_NAME}_{MM-DD-YYYY}.yaml
 * Example: PGUOXED1_01-05-2026.yaml
 * 
 * Directory structure:
 *   algorithms/
 *     {insurance_line}/
 *       {MODULE_NAME}_{effective_date}.yaml
 */
public class AlgorithmLoader
{
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private static final String YAML_EXTENSION = ".yaml";
    
    private final Path baseDirectory;
    
    public AlgorithmLoader(Path baseDirectory)
    {
        this.baseDirectory = baseDirectory;
    }
    
    public AlgorithmLoader(String baseDirectory)
    {
        this(Path.of(baseDirectory));
    }
    
    /**
     * Loads an algorithm by insurance line, module name, and effective date.
     */
    public Optional<AlgorithmModule> load(String insuranceLine, String moduleName, LocalDate effectiveDate)
    {
        Path filePath = buildFilePath(insuranceLine, moduleName, effectiveDate);
        
        if (!Files.exists(filePath))
        {
            return Optional.empty();
        }
        
        return loadFromFile(filePath);
    }
    
    /**
     * Finds the algorithm effective on the given date.
     * Searches for the most recent version where effectiveDate <= targetDate.
     */
    public Optional<AlgorithmModule> findEffective(String insuranceLine, String moduleName, LocalDate targetDate)
    {
        Path lineDir = baseDirectory.resolve(insuranceLine);
        
        if (!Files.exists(lineDir))
        {
            return Optional.empty();
        }
        
        try (Stream<Path> files = Files.list(lineDir))
        {
            String prefix = moduleName + "_";
            
            return files
                .filter(p -> p.getFileName().toString().startsWith(prefix))
                .filter(p -> p.getFileName().toString().endsWith(YAML_EXTENSION))
                .map(this::extractDateFromFilename)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(date -> !date.isAfter(targetDate))
                .max(LocalDate::compareTo)
                .flatMap(date -> load(insuranceLine, moduleName, date));
        }
        catch (IOException e)
        {
            return Optional.empty();
        }
    }
    
    /**
     * Lists all module names for an insurance line.
     */
    public List<String> listModules(String insuranceLine)
    {
        Path lineDir = baseDirectory.resolve(insuranceLine);
        List<String> modules = new ArrayList<>();
        
        if (!Files.exists(lineDir))
        {
            return modules;
        }
        
        try (Stream<Path> files = Files.list(lineDir))
        {
            files
                .filter(p -> p.getFileName().toString().endsWith(YAML_EXTENSION))
                .map(p -> extractModuleName(p.getFileName().toString()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .sorted()
                .forEach(modules::add);
        }
        catch (IOException e)
        {
            // Return empty list on error
        }
        
        return modules;
    }
    
    /**
     * Lists all insurance lines (subdirectories).
     */
    public List<String> listInsuranceLines()
    {
        List<String> lines = new ArrayList<>();
        
        if (!Files.exists(baseDirectory))
        {
            return lines;
        }
        
        try (Stream<Path> dirs = Files.list(baseDirectory))
        {
            dirs
                .filter(Files::isDirectory)
                .map(p -> p.getFileName().toString())
                .sorted()
                .forEach(lines::add);
        }
        catch (IOException e)
        {
            // Return empty list on error
        }
        
        return lines;
    }
    
    /**
     * Saves an algorithm module to a YAML file.
     */
    public void save(AlgorithmModule module) throws IOException
    {
        Path filePath = buildFilePath(
            module.getInsuranceLineCode(),
            module.getModuleName(),
            module.getEffectiveDate()
        );
        
        // Ensure directory exists
        Files.createDirectories(filePath.getParent());
        
        Map<String, Object> yamlMap = toYamlMap(module);
        
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setIndent(2);
        
        Yaml yaml = new Yaml(options);
        
        try (Writer writer = Files.newBufferedWriter(filePath))
        {
            yaml.dump(yamlMap, writer);
        }
    }
    
    /**
     * Builds the filename for an algorithm: {MODULE_NAME}_{MM-DD-YYYY}.yaml
     */
    public String buildFilename(String moduleName, LocalDate effectiveDate)
    {
        return moduleName + "_" + effectiveDate.format(DATE_FORMAT) + YAML_EXTENSION;
    }
    
    // ========== Private Methods ==========
    
    private Path buildFilePath(String insuranceLine, String moduleName, LocalDate effectiveDate)
    {
        String filename = buildFilename(moduleName, effectiveDate);
        return baseDirectory.resolve(insuranceLine).resolve(filename);
    }
    
    private Optional<LocalDate> extractDateFromFilename(Path path)
    {
        String filename = path.getFileName().toString();
        int underscoreIdx = filename.lastIndexOf('_');
        
        if (underscoreIdx < 0)
        {
            return Optional.empty();
        }
        
        String dateStr = filename.substring(underscoreIdx + 1, filename.length() - YAML_EXTENSION.length());
        
        try
        {
            return Optional.of(LocalDate.parse(dateStr, DATE_FORMAT));
        }
        catch (Exception e)
        {
            return Optional.empty();
        }
    }
    
    private Optional<String> extractModuleName(String filename)
    {
        int underscoreIdx = filename.lastIndexOf('_');
        
        if (underscoreIdx < 0)
        {
            return Optional.empty();
        }
        
        return Optional.of(filename.substring(0, underscoreIdx));
    }
    
    private Optional<AlgorithmModule> loadFromFile(Path filePath)
    {
        try (Reader reader = Files.newBufferedReader(filePath))
        {
            Yaml yaml = new Yaml();
            Map<String, Object> map = yaml.load(reader);
            return Optional.of(fromYamlMap(map));
        }
        catch (IOException e)
        {
            return Optional.empty();
        }
    }
    
    @SuppressWarnings("unchecked")
    private AlgorithmModule fromYamlMap(Map<String, Object> map)
    {
        AlgorithmModule module = new AlgorithmModule();
        
        module.setModuleName((String) map.get("moduleName"));
        module.setInsuranceLineCode((String) map.get("insuranceLine"));
        module.setDescription((String) map.get("description"));
        
        String effectiveDateStr = (String) map.get("effectiveDate");
        if (effectiveDateStr != null)
        {
            module.setEffectiveDate(LocalDate.parse(effectiveDateStr));
        }
        
        module.setActive(getBooleanValue(map, "active", true));
        
        // Load steps
        List<Map<String, Object>> stepsList = (List<Map<String, Object>>) map.get("steps");
        if (stepsList != null)
        {
            List<AlgorithmStep> steps = new ArrayList<>();
            for (Map<String, Object> stepMap : stepsList)
            {
                steps.add(stepFromYamlMap(stepMap));
            }
            module.setSteps(steps);
        }
        
        return module;
    }
    
    private AlgorithmStep stepFromYamlMap(Map<String, Object> map)
    {
        AlgorithmStep step = new AlgorithmStep();
        
        step.setSequence(getIntValue(map, "sequence", 0));
        step.setStepType(getIntValue(map, "stepType", 0));
        step.setElementId((String) map.get("elementId"));
        step.setDescription((String) map.get("description"));
        
        // Value type
        String valueTypeStr = (String) map.get("valueType");
        if (valueTypeStr != null)
        {
            step.setValueType(ValueType.valueOf(valueTypeStr));
        }
        
        // Operator
        String operatorStr = (String) map.get("operator");
        if (operatorStr != null)
        {
            step.setOperator(Operator.valueOf(operatorStr));
        }
        
        // Rounding
        String roundingStr = (String) map.get("rounding");
        if (roundingStr != null)
        {
            step.setRounding(RoundingType.valueOf(roundingStr));
        }
        
        step.setDecimalPlaces(getIntValue(map, "decimalPlaces", 0));
        
        // Literal value
        Object literalValue = map.get("literalValue");
        if (literalValue != null)
        {
            step.setLiteralValue(new BigDecimal(literalValue.toString()));
        }
        
        // Code reference
        step.setCodeTable((String) map.get("codeTable"));
        step.setCodeValue((String) map.get("codeValue"));
        
        // Database reference
        step.setDbTable((String) map.get("dbTable"));
        step.setDbColumn((String) map.get("dbColumn"));
        
        // Control flow
        step.setTargetStep(getIntValue(map, "targetStep", 0));
        step.setCompareElementId((String) map.get("compareElementId"));
        
        return step;
    }
    
    private Map<String, Object> toYamlMap(AlgorithmModule module)
    {
        Map<String, Object> map = new LinkedHashMap<>();
        
        map.put("moduleName", module.getModuleName());
        map.put("insuranceLine", module.getInsuranceLineCode());
        map.put("description", module.getDescription());
        
        if (module.getEffectiveDate() != null)
        {
            map.put("effectiveDate", module.getEffectiveDate().toString());
        }
        
        map.put("active", module.isActive());
        
        // Steps
        List<Map<String, Object>> stepsList = new ArrayList<>();
        if (module.getSteps() != null)
        {
            for (AlgorithmStep step : module.getSteps())
            {
                stepsList.add(stepToYamlMap(step));
            }
        }
        map.put("steps", stepsList);
        
        return map;
    }
    
    private Map<String, Object> stepToYamlMap(AlgorithmStep step)
    {
        Map<String, Object> map = new LinkedHashMap<>();
        
        map.put("sequence", step.getSequence());
        map.put("stepType", step.getStepType());
        
        if (step.getElementId() != null)
        {
            map.put("elementId", step.getElementId());
        }
        
        if (step.getDescription() != null)
        {
            map.put("description", step.getDescription());
        }
        
        if (step.getValueType() != null)
        {
            map.put("valueType", step.getValueType().name());
        }
        
        if (step.getOperator() != null && step.getOperator() != Operator.NONE)
        {
            map.put("operator", step.getOperator().name());
        }
        
        if (step.getRounding() != null && step.getRounding() != RoundingType.NONE)
        {
            map.put("rounding", step.getRounding().name());
        }
        
        if (step.getDecimalPlaces() > 0)
        {
            map.put("decimalPlaces", step.getDecimalPlaces());
        }
        
        if (step.getLiteralValue() != null)
        {
            map.put("literalValue", step.getLiteralValue().toString());
        }
        
        if (step.getCodeTable() != null)
        {
            map.put("codeTable", step.getCodeTable());
        }
        
        if (step.getCodeValue() != null)
        {
            map.put("codeValue", step.getCodeValue());
        }
        
        if (step.getDbTable() != null)
        {
            map.put("dbTable", step.getDbTable());
        }
        
        if (step.getDbColumn() != null)
        {
            map.put("dbColumn", step.getDbColumn());
        }
        
        if (step.getTargetStep() > 0)
        {
            map.put("targetStep", step.getTargetStep());
        }
        
        if (step.getCompareElementId() != null)
        {
            map.put("compareElementId", step.getCompareElementId());
        }
        
        return map;
    }
    
    private boolean getBooleanValue(Map<String, Object> map, String key, boolean defaultValue)
    {
        Object value = map.get(key);
        if (value instanceof Boolean)
        {
            return (Boolean) value;
        }
        return defaultValue;
    }
    
    private int getIntValue(Map<String, Object> map, String key, int defaultValue)
    {
        Object value = map.get(key);
        if (value instanceof Number)
        {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }
}
