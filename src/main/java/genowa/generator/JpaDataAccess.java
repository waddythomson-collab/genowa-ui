package genowa.generator;

import genowa.model.WarpCtl;
import genowa.model.WarpCtlData;
import genowa.model.WarpCtlId;
import genowa.model.WarpCtlDataId;
import genowa.model.DbTable;
import genowa.model.DbColumn;
import genowa.model.LevelType;
import genowa.model.ElementValue;
import genowa.model.RadCtlItem;
import genowa.model.GenDataModels.*;
import genowa.util.IString;
import jakarta.persistence.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * JPA-based implementation of DataAccess interface.
 * Uses Hibernate/JPA for database operations.
 */
public class JpaDataAccess implements DataAccess
{
    private static final Logger logger = Logger.getLogger(JpaDataAccess.class.getName());
    
    private EntityManagerFactory emf;
    private EntityManager em;
    private String lastError = "";
    private boolean connected = false;
    
    // Cached collections
    private List<GenTable> genTables = new ArrayList<>();
    private List<GenField> genFields = new ArrayList<>();
    private List<GenModuleType> genModuleTypes = new ArrayList<>();
    private List<GenAlgorithm> genAlgorithms = new ArrayList<>();
    private List<GenSequence> genSequences = new ArrayList<>();
    private List<GenRule> genRules = new ArrayList<>();
    private List<GenTemplate> genTemplates = new ArrayList<>();
    private List<GenTableType> genTableTypes = new ArrayList<>();
    private List<GenApplication> genApplications = new ArrayList<>();
    private List<GenLinkageType> genLinkageTypes = new ArrayList<>();
    private List<GenDataType> genDataTypes = new ArrayList<>();
    
    /**
     * Default constructor - uses persistence.xml configuration.
     */
    public JpaDataAccess()
    {
    }
    
    /**
     * Constructor with existing EntityManagerFactory.
     */
    public JpaDataAccess(EntityManagerFactory emf)
    {
        this.emf = emf;
        this.em = emf.createEntityManager();
        this.connected = true;
    }
    
    @Override
    public boolean connect(String connectionString)
    {
        try
        {
            // Parse connection string or use default persistence unit
            Map<String, String> props = new HashMap<>();
            if (connectionString != null && !connectionString.isEmpty())
            {
                props.put("jakarta.persistence.jdbc.url", connectionString);
            }
            
            emf = Persistence.createEntityManagerFactory("genowa", props);
            em = emf.createEntityManager();
            connected = true;
            lastError = "";
            logger.info("JPA connected successfully");
            return true;
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.severe("Failed to connect: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean connect(IString connectionString)
    {
        return connect(connectionString != null ? connectionString.str() : "");
    }
    
    @Override
    public void disconnect()
    {
        if (em != null && em.isOpen())
        {
            em.close();
        }
        if (emf != null && emf.isOpen())
        {
            emf.close();
        }
        connected = false;
        logger.info("JPA disconnected");
    }
    
    @Override
    public boolean isConnected()
    {
        return connected && em != null && em.isOpen();
    }
    
    @Override
    public String getLastError()
    {
        return lastError;
    }
    
    @Override
    public IString getLastErrorIString()
    {
        return new IString(lastError);
    }
    
    // ========== Business data methods ==========
    
    @Override
    public List<String> getTableNames()
    {
        List<String> tableNames = new ArrayList<>();
        try
        {
            String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
            Query query = em.createNativeQuery(sql);
            @SuppressWarnings("unchecked")
            List<String> results = query.getResultList();
            tableNames.addAll(results);
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.warning("Error getting table names: " + e.getMessage());
        }
        return tableNames;
    }
    
    @Override
    public List<String> getColumnNames(String tableName)
    {
        List<String> columnNames = new ArrayList<>();
        try
        {
            String sql = "SELECT column_name FROM information_schema.columns WHERE table_name = :tableName";
            Query query = em.createNativeQuery(sql);
            query.setParameter("tableName", tableName);
            @SuppressWarnings("unchecked")
            List<String> results = query.getResultList();
            columnNames.addAll(results);
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.warning("Error getting column names: " + e.getMessage());
        }
        return columnNames;
    }
    
    @Override
    public Map<String, String> getColumnTypes(String tableName)
    {
        Map<String, String> columnTypes = new HashMap<>();
        try
        {
            String sql = "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = :tableName";
            Query query = em.createNativeQuery(sql);
            query.setParameter("tableName", tableName);
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            for (Object[] row : results)
            {
                columnTypes.put((String) row[0], (String) row[1]);
            }
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.warning("Error getting column types: " + e.getMessage());
        }
        return columnTypes;
    }
    
    // ========== GEN_* table loading methods ==========
    
    @Override
    public boolean loadGenTables()
    {
        try
        {
            TypedQuery<GenTable> query = em.createQuery(
                "SELECT t FROM GenTable t ORDER BY t.tableName", GenTable.class);
            genTables = query.getResultList();
            logger.info("Loaded " + genTables.size() + " GenTables");
            return true;
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.warning("Error loading GenTables: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean loadGenFields(int tableId)
    {
        try
        {
            TypedQuery<GenField> query = em.createQuery(
                "SELECT f FROM GenField f WHERE f.tableId = :tableId ORDER BY f.fieldName", 
                GenField.class);
            query.setParameter("tableId", tableId);
            List<GenField> fields = query.getResultList();
            // Merge into main collection
            genFields.removeIf(f -> f.tableId == tableId);
            genFields.addAll(fields);
            logger.info("Loaded " + fields.size() + " GenFields for table " + tableId);
            return true;
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.warning("Error loading GenFields: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean loadGenModuleTypes()
    {
        try
        {
            TypedQuery<GenModuleType> query = em.createQuery(
                "SELECT m FROM GenModuleType m ORDER BY m.moduleTypeCode", GenModuleType.class);
            genModuleTypes = query.getResultList();
            logger.info("Loaded " + genModuleTypes.size() + " GenModuleTypes");
            return true;
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.warning("Error loading GenModuleTypes: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean loadGenAlgorithms()
    {
        try
        {
            TypedQuery<GenAlgorithm> query = em.createQuery(
                "SELECT a FROM GenAlgorithm a ORDER BY a.algorithmName", GenAlgorithm.class);
            genAlgorithms = query.getResultList();
            logger.info("Loaded " + genAlgorithms.size() + " GenAlgorithms");
            return true;
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.warning("Error loading GenAlgorithms: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean loadGenSequences()
    {
        try
        {
            TypedQuery<GenSequence> query = em.createQuery(
                "SELECT s FROM GenSequence s ORDER BY s.sequenceName", GenSequence.class);
            genSequences = query.getResultList();
            logger.info("Loaded " + genSequences.size() + " GenSequences");
            return true;
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.warning("Error loading GenSequences: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean loadGenRules()
    {
        try
        {
            TypedQuery<GenRule> query = em.createQuery(
                "SELECT r FROM GenRule r ORDER BY r.ruleName", GenRule.class);
            genRules = query.getResultList();
            logger.info("Loaded " + genRules.size() + " GenRules");
            return true;
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.warning("Error loading GenRules: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean loadGenTemplates()
    {
        try
        {
            TypedQuery<GenTemplate> query = em.createQuery(
                "SELECT t FROM GenTemplate t ORDER BY t.templateName", GenTemplate.class);
            genTemplates = query.getResultList();
            logger.info("Loaded " + genTemplates.size() + " GenTemplates");
            return true;
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.warning("Error loading GenTemplates: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean loadGenTableTypes()
    {
        try
        {
            TypedQuery<GenTableType> query = em.createQuery(
                "SELECT t FROM GenTableType t ORDER BY t.typeCode", GenTableType.class);
            genTableTypes = query.getResultList();
            logger.info("Loaded " + genTableTypes.size() + " GenTableTypes");
            return true;
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.warning("Error loading GenTableTypes: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean loadGenApplications()
    {
        try
        {
            TypedQuery<GenApplication> query = em.createQuery(
                "SELECT a FROM GenApplication a ORDER BY a.applicationCode", GenApplication.class);
            genApplications = query.getResultList();
            logger.info("Loaded " + genApplications.size() + " GenApplications");
            return true;
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.warning("Error loading GenApplications: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean loadGenLinkageTypes()
    {
        try
        {
            TypedQuery<GenLinkageType> query = em.createQuery(
                "SELECT l FROM GenLinkageType l ORDER BY l.linkageTypeCode", GenLinkageType.class);
            genLinkageTypes = query.getResultList();
            logger.info("Loaded " + genLinkageTypes.size() + " GenLinkageTypes");
            return true;
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.warning("Error loading GenLinkageTypes: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean loadGenDataTypes()
    {
        try
        {
            TypedQuery<GenDataType> query = em.createQuery(
                "SELECT d FROM GenDataType d ORDER BY d.dataTypeCode", GenDataType.class);
            genDataTypes = query.getResultList();
            logger.info("Loaded " + genDataTypes.size() + " GenDataTypes");
            return true;
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.warning("Error loading GenDataTypes: " + e.getMessage());
            return false;
        }
    }
    
    // ========== Getter methods ==========
    
    @Override
    public List<GenTable> getGenTables()
    {
        return new ArrayList<>(genTables);
    }
    
    @Override
    public List<GenField> getGenFields(int tableId)
    {
        List<GenField> result = new ArrayList<>();
        for (GenField f : genFields)
        {
            if (f.tableId == tableId)
            {
                result.add(f);
            }
        }
        return result;
    }
    
    @Override
    public List<GenModuleType> getGenModuleTypes()
    {
        return new ArrayList<>(genModuleTypes);
    }
    
    @Override
    public List<GenAlgorithm> getGenAlgorithms()
    {
        return new ArrayList<>(genAlgorithms);
    }
    
    @Override
    public List<GenSequence> getGenSequences()
    {
        return new ArrayList<>(genSequences);
    }
    
    @Override
    public List<GenRule> getGenRules()
    {
        return new ArrayList<>(genRules);
    }
    
    @Override
    public List<GenTemplate> getGenTemplates()
    {
        return new ArrayList<>(genTemplates);
    }
    
    // ========== Application and linkage data ==========
    
    @Override
    public Map<String, String> loadApplicationPrefixes()
    {
        Map<String, String> prefixes = new HashMap<>();
        for (GenApplication app : genApplications)
        {
            prefixes.put(app.applicationCode != null ? app.applicationCode.str() : "", 
                        app.applicationName != null ? app.applicationName.str() : "");
        }
        return prefixes;
    }
    
    @Override
    public Map<String, String> loadLinkageTypes()
    {
        Map<String, String> types = new HashMap<>();
        for (GenLinkageType lt : genLinkageTypes)
        {
            types.put(lt.linkageTypeCode != null ? lt.linkageTypeCode.str() : "", 
                     lt.linkageTypeName != null ? lt.linkageTypeName.str() : "");
        }
        return types;
    }
    
    // ========== Table lookup methods ==========
    
    @Override
    public GenTable findGenTableByName(String tableName)
    {
        for (GenTable t : genTables)
        {
            if (t.tableName != null && tableName.equalsIgnoreCase(t.tableName.str()))
            {
                return t;
            }
        }
        return null;
    }
    
    @Override
    public GenTable findGenTableById(int tableId)
    {
        for (GenTable t : genTables)
        {
            if (t.tableId == tableId)
            {
                return t;
            }
        }
        return null;
    }
    
    @Override
    public List<GenField> getGenTableFields(int tableId)
    {
        return getGenFields(tableId);
    }
    
    @Override
    public List<GenRule> getGenTableRules(int tableId)
    {
        List<GenRule> result = new ArrayList<>();
        for (GenRule r : genRules)
        {
            if (r.tableId == tableId)
            {
                result.add(r);
            }
        }
        return result;
    }
    
    // ========== Algorithm and sequence lookup ==========
    
    @Override
    public GenAlgorithm findGenAlgorithmByName(String algorithmName)
    {
        for (GenAlgorithm a : genAlgorithms)
        {
            if (a.algorithmName != null && algorithmName.equalsIgnoreCase(a.algorithmName.str()))
            {
                return a;
            }
        }
        return null;
    }
    
    @Override
    public GenSequence findGenSequenceByName(String sequenceName)
    {
        for (GenSequence s : genSequences)
        {
            if (s.sequenceName != null && sequenceName.equalsIgnoreCase(s.sequenceName.str()))
            {
                return s;
            }
        }
        return null;
    }
    
    // ========== Runtime SQL generation methods ==========
    
    @Override
    public String generateSelectSQL(String tableName, List<String> columns, String whereClause)
    {
        StringBuilder sql = new StringBuilder("SELECT ");
        
        if (columns == null || columns.isEmpty())
        {
            sql.append("*");
        }
        else
        {
            sql.append(String.join(", ", columns));
        }
        
        sql.append(" FROM ").append(tableName);
        
        if (whereClause != null && !whereClause.isEmpty())
        {
            sql.append(" WHERE ").append(whereClause);
        }
        
        return sql.toString();
    }
    
    @Override
    public String generateInsertSQL(String tableName, List<String> columns)
    {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(tableName).append(" (");
        sql.append(String.join(", ", columns));
        sql.append(") VALUES (");
        
        List<String> placeholders = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++)
        {
            placeholders.add("?");
        }
        sql.append(String.join(", ", placeholders));
        sql.append(")");
        
        return sql.toString();
    }
    
    @Override
    public String generateUpdateSQL(String tableName, List<String> columns, String whereClause)
    {
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(tableName).append(" SET ");
        
        List<String> assignments = new ArrayList<>();
        for (String col : columns)
        {
            assignments.add(col + " = ?");
        }
        sql.append(String.join(", ", assignments));
        
        if (whereClause != null && !whereClause.isEmpty())
        {
            sql.append(" WHERE ").append(whereClause);
        }
        
        return sql.toString();
    }
    
    @Override
    public String generateDeleteSQL(String tableName, String whereClause)
    {
        StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append(tableName);
        
        if (whereClause != null && !whereClause.isEmpty())
        {
            sql.append(" WHERE ").append(whereClause);
        }
        
        return sql.toString();
    }
    
    // ========== Dynamic table operations ==========
    
    @Override
    public boolean executeDynamicSQL(String sql, List<String> params)
    {
        try
        {
            em.getTransaction().begin();
            Query query = em.createNativeQuery(sql);
            if (params != null)
            {
                for (int i = 0; i < params.size(); i++)
                {
                    query.setParameter(i + 1, params.get(i));
                }
            }
            query.executeUpdate();
            em.getTransaction().commit();
            return true;
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            if (em.getTransaction().isActive())
            {
                em.getTransaction().rollback();
            }
            logger.warning("Error executing dynamic SQL: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<Map<String, String>> executeDynamicQuery(String sql, List<String> params)
    {
        List<Map<String, String>> results = new ArrayList<>();
        try
        {
            Query query = em.createNativeQuery(sql);
            if (params != null)
            {
                for (int i = 0; i < params.size(); i++)
                {
                    query.setParameter(i + 1, params.get(i));
                }
            }
            
            @SuppressWarnings("unchecked")
            List<Object[]> rows = query.getResultList();
            
            // Note: Without column metadata, we'll use positional keys
            for (Object[] row : rows)
            {
                Map<String, String> rowMap = new HashMap<>();
                for (int i = 0; i < row.length; i++)
                {
                    rowMap.put("col_" + i, row[i] != null ? row[i].toString() : null);
                }
                results.add(rowMap);
            }
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.warning("Error executing dynamic query: " + e.getMessage());
        }
        return results;
    }
    
    // ========== RadCtl data methods ==========
    
    @Override
    public List<RadCtlItem> getRadCtlData(String insuranceLine)
    {
        try
        {
            TypedQuery<RadCtlItem> query = em.createQuery(
                "SELECT r FROM RadCtlItem r WHERE r.insuranceLineCd = :insuranceLine ORDER BY r.sequenceNbr",
                RadCtlItem.class);
            query.setParameter("insuranceLine", insuranceLine);
            return query.getResultList();
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.warning("Error getting RadCtl data: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<RadCtlItem> getRadCtlDataByTable(String insuranceLine, String tableName)
    {
        try
        {
            TypedQuery<RadCtlItem> query = em.createQuery(
                "SELECT r FROM RadCtlItem r WHERE r.insuranceLineCd = :insuranceLine " +
                "AND r.tableNm = :tableName ORDER BY r.sequenceNbr",
                RadCtlItem.class);
            query.setParameter("insuranceLine", insuranceLine);
            query.setParameter("tableName", tableName);
            return query.getResultList();
        }
        catch (Exception e)
        {
            lastError = e.getMessage();
            logger.warning("Error getting RadCtl data by table: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // ========== Configuration management ==========
    
    @Override
    public boolean reloadConfiguration()
    {
        boolean success = true;
        success &= loadGenTableTypes();
        success &= loadGenApplications();
        success &= loadGenLinkageTypes();
        success &= loadGenDataTypes();
        success &= loadGenTables();
        success &= loadGenModuleTypes();
        success &= loadGenAlgorithms();
        success &= loadGenSequences();
        success &= loadGenRules();
        success &= loadGenTemplates();
        return success;
    }
    
    @Override
    public String getConfigurationStatus()
    {
        StringBuilder status = new StringBuilder();
        status.append("GenTables: ").append(genTables.size()).append("\n");
        status.append("GenFields: ").append(genFields.size()).append("\n");
        status.append("GenModuleTypes: ").append(genModuleTypes.size()).append("\n");
        status.append("GenAlgorithms: ").append(genAlgorithms.size()).append("\n");
        status.append("GenSequences: ").append(genSequences.size()).append("\n");
        status.append("GenRules: ").append(genRules.size()).append("\n");
        status.append("GenTemplates: ").append(genTemplates.size()).append("\n");
        return status.toString();
    }
}
