package genowa.generator;

import genowa.model.RadCtlItem;

import genowa.model.GenDataModels.*;
import genowa.util.IString;
import java.util.List;
import java.util.Map;

/**
 * Data access interface for Genowa code generation.
 * Maps to C++ DataAccess abstract class.
 */
public interface DataAccess
{
    // Connection management
    boolean connect(String connectionString);
    boolean connect(IString connectionString);
    void disconnect();
    boolean isConnected();
    String getLastError();
    IString getLastErrorIString();
    
    // Business data methods (external database)
    List<String> getTableNames();
    List<String> getColumnNames(String tableName);
    Map<String, String> getColumnTypes(String tableName);
    
    // Core GEN_* table loading methods
    boolean loadGenTables();
    boolean loadGenFields(int tableId);
    boolean loadGenModuleTypes();
    boolean loadGenAlgorithms();
    boolean loadGenSequences();
    boolean loadGenRules();
    boolean loadGenTemplates();
    
    // Additional GEN_* table loading methods
    boolean loadGenTableTypes();
    boolean loadGenApplications();
    boolean loadGenLinkageTypes();
    boolean loadGenDataTypes();
    
    // Legacy methods for backward compatibility
    List<GenTable> getGenTables();
    List<GenField> getGenFields(int tableId);
    List<GenModuleType> getGenModuleTypes();
    List<GenAlgorithm> getGenAlgorithms();
    List<GenSequence> getGenSequences();
    List<GenRule> getGenRules();
    List<GenTemplate> getGenTemplates();
    
    // Application and linkage data
    Map<String, String> loadApplicationPrefixes();
    Map<String, String> loadLinkageTypes();
    
    // Table lookup methods
    GenTable findGenTableByName(String tableName);
    GenTable findGenTableById(int tableId);
    List<GenField> getGenTableFields(int tableId);
    List<GenRule> getGenTableRules(int tableId);
    
    // Algorithm and sequence lookup
    GenAlgorithm findGenAlgorithmByName(String algorithmName);
    GenSequence findGenSequenceByName(String sequenceName);
    
    // Runtime SQL generation methods
    String generateSelectSQL(String tableName, List<String> columns, String whereClause);
    String generateInsertSQL(String tableName, List<String> columns);
    String generateUpdateSQL(String tableName, List<String> columns, String whereClause);
    String generateDeleteSQL(String tableName, String whereClause);
    
    // Dynamic table operations
    boolean executeDynamicSQL(String sql, List<String> params);
    List<Map<String, String>> executeDynamicQuery(String sql, List<String> params);
    
    // RadCtl data methods (for existing functionality)
    List<RadCtlItem> getRadCtlData(String insuranceLine);
    List<RadCtlItem> getRadCtlDataByTable(String insuranceLine, String tableName);
    
    // Configuration management
    boolean reloadConfiguration();
    String getConfigurationStatus();
}
