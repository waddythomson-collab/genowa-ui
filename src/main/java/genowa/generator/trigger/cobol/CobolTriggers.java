package genowa.generator.trigger.cobol;

import genowa.generator.GenCore;
import genowa.generator.trigger.Trigger;
import genowa.generator.trigger.TriggerRegistry;
import genowa.model.GenField;
import genowa.model.GenTable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * COBOL trigger implementations.
 * Converted from C++ IOC generator triggers.
 */
public class CobolTriggers
{
    // Shared variable storage for VAR1-VAR4
    private static final Map<String, String> variables = new HashMap<>();
    
    // Entity manager for database access
    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    
    // Generation context (set by generator before processing)
    private static int currentTableNbr = 0;
    private static String currentTableName = "";
    private static String programName = "PROGRAM";
    private static String linkagePrefix = "";
    private static String hostVarPrefix = "H";
    private static String insuranceLineCd = "";
    
    /**
     * Initialize database connection for triggers.
     */
    public static void initDatabase()
    {
        if (emf == null)
        {
            emf = Persistence.createEntityManagerFactory("genowa");
        }
        if (em == null || !em.isOpen())
        {
            em = emf.createEntityManager();
        }
    }
    
    /**
     * Close database connection.
     */
    public static void closeDatabase()
    {
        if (em != null && em.isOpen())
        {
            em.close();
        }
    }
    
    /**
     * Get the entity manager for database queries.
     */
    public static EntityManager getEntityManager()
    {
        if (em == null || !em.isOpen())
        {
            initDatabase();
        }
        return em;
    }
    
    /**
     * Set the current table context for triggers.
     */
    public static void setTableContext(int tableNbr, String tableName)
    {
        currentTableNbr = tableNbr;
        currentTableName = tableName;
    }
    
    /**
     * Set the program name for &PGM| trigger.
     */
    public static void setProgramName(String name)
    {
        programName = name;
    }
    
    /**
     * Set the linkage prefix for &LINKPR| and &PREFIX| triggers.
     */
    public static void setLinkagePrefix(String prefix)
    {
        linkagePrefix = prefix;
    }
    
    /**
     * Set the host variable prefix for SQL triggers.
     */
    public static void setHostVarPrefix(String prefix)
    {
        hostVarPrefix = prefix;
    }
    
    /**
     * Set the insurance line code.
     */
    public static void setInsuranceLineCd(String code)
    {
        insuranceLineCd = code;
    }
    
    /**
     * Set a variable value (VAR1-VAR4).
     */
    public static void setVariable(String name, String value)
    {
        variables.put(name, value);
    }
    
    /**
     * Get a variable value.
     */
    public static String getVariable(String name)
    {
        return variables.getOrDefault(name, "");
    }
    
    /**
     * Register all COBOL triggers with the registry.
     */
    public static void registerAll(TriggerRegistry registry)
    {
        // ============================================================
        // Basic/Naming Triggers
        // ============================================================
        registry.registerSingleton("PGM", new PgmTrigger());
        registry.registerSingleton("DPGM", new DpgmTrigger());
        registry.registerSingleton("TBL", new TblTrigger());
        registry.registerSingleton("PREFIX", new PrefixTrigger());
        registry.registerSingleton("LINKPR", new LinkprTrigger());
        registry.registerSingleton("COBOLNM", new CobolNmTrigger());
        registry.registerSingleton("COBOLWSNM", new CobolWsNmTrigger());
        
        // ============================================================
        // Date/Time Triggers
        // ============================================================
        registry.registerSingleton("WDATE", new WDateTrigger());
        registry.registerSingleton("WTIME", new WTimeTrigger());
        registry.registerSingleton("DATEINPFORMAT", new DateInpFormatTrigger());
        
        // ============================================================
        // Variable Triggers
        // ============================================================
        registry.registerSingleton("VAR1", new VarTrigger("VAR1"));
        registry.registerSingleton("VAR2", new VarTrigger("VAR2"));
        registry.registerSingleton("VAR3", new VarTrigger("VAR3"));
        registry.registerSingleton("VAR4", new VarTrigger("VAR4"));
        
        // ============================================================
        // SQL Triggers
        // ============================================================
        registry.registerSingleton("SQLC", new SqlcTrigger());
        registry.registerSingleton("SQLF", new SqlfTrigger());
        registry.registerSingleton("SQLI", new SqliTrigger());
        registry.registerSingleton("SQLINCL2", new SqlIncl2Trigger());
        registry.registerSingleton("SQLINCL3", new SqlIncl3Trigger());
        
        // ============================================================
        // Key Triggers
        // ============================================================
        registry.registerSingleton("FULLKEY", new FullkeyTrigger());
        registry.registerSingleton("SETKEY", new SetkeyTrigger());
        registry.registerSingleton("BUILDSTDKEY", new BuildStdKeyTrigger());
        registry.registerSingleton("UPDATEKEY", new UpdateKeyTrigger());
        registry.registerSingleton("KEYID", new KeyIdTrigger());
        
        // ============================================================
        // Host Variable Triggers
        // ============================================================
        registry.registerSingleton("HOST_", new HostTrigger());
        registry.registerSingleton("MOVEHV2", new Movehv2Trigger());
        
        // ============================================================
        // Working Storage Triggers
        // ============================================================
        registry.registerSingleton("WSVARS", new WsvarsTrigger());
        registry.registerSingleton("WORKSTATION", new WorkstationTrigger());
        
        // ============================================================
        // IO Module Triggers
        // ============================================================
        registry.registerSingleton("IOMODULE", new IoModuleTrigger());
        registry.registerSingleton("IOMODULECOPYBOOKS", new IoModuleCopybooksTrigger());
        registry.registerSingleton("IOPARAGRAPHS", new IoParagraphsTrigger());
        
        // ============================================================
        // Rating Triggers
        // ============================================================
        registry.registerSingleton("RATEKEYDEF", new RateKeyDefTrigger());
        registry.registerSingleton("RATEELEDEF", new RateEleDefTrigger());
        registry.registerSingleton("ELEDESC", new EleDescTrigger());
        
        // ============================================================
        // Conditional Triggers
        // ============================================================
        registry.registerSingleton("<", new IncludeTrigger());
        registry.registerSingleton("!", new ExcludeTrigger());
        registry.registerSingleton("IF", new IfTrigger());
        registry.registerSingleton("ELSE", new ElseTrigger());
        registry.registerSingleton("ENDIF", new EndIfTrigger());
        registry.registerSingleton("IFINSLINE", new IfInsLineTrigger());
        
        // ============================================================
        // Code Generation Triggers
        // ============================================================
        registry.registerSingleton("MOVE", new MoveTrigger());
        registry.registerSingleton("PERFORM", new PerformTrigger());
        registry.registerSingleton("CODE", new CodeTrigger());
        registry.registerSingleton("CMNT", new CmntTrigger());
        registry.registerSingleton("TEMPLATE", new TemplateTrigger());
        registry.registerSingleton("SKIP", new SkipTrigger());
        
        // ============================================================
        // CICS/Batch Triggers
        // ============================================================
        registry.registerSingleton("CICS_", new CicsTrigger());
        registry.registerSingleton("BATCH", new BatchTrigger());
        
        // ============================================================
        // Other Triggers
        // ============================================================
        registry.registerSingleton("QUOTESEQNBR", new QuoteSequenceNbrTrigger());
        registry.registerSingleton("DELROWSIND", new DelRowsIndTrigger());
        registry.registerSingleton("SECURAASOFWHERE", new SecuraAsOfWhereTrigger());
        registry.registerSingleton("WCADT", new WcAdtTrigger());
    }
    
    // ============================================================
    // BASIC/NAMING TRIGGERS
    // ============================================================
    
    /**
     * &PGM| - Program name trigger.
     * Returns the current program name.
     */
    public static class PgmTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return programName;
        }
        
        @Override
        public String getKeyword()
        {
            return "PGM";
        }
    }
    
    /**
     * &DPGM| - Debug program name trigger.
     * Returns program name for debug/display purposes.
     */
    public static class DpgmTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return "D" + programName;
        }
        
        @Override
        public String getKeyword()
        {
            return "DPGM";
        }
    }
    
    /**
     * &TBL| - Table name trigger.
     * Returns the current table name.
     */
    public static class TblTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return currentTableName;
        }
        
        @Override
        public String getKeyword()
        {
            return "TBL";
        }
    }
    
    /**
     * &PREFIX| - Prefix trigger.
     * Returns the current prefix (typically host variable prefix).
     */
    public static class PrefixTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return hostVarPrefix;
        }
        
        @Override
        public String getKeyword()
        {
            return "PREFIX";
        }
    }
    
    /**
     * &LINKPR| - Linkage prefix trigger.
     * Returns the linkage section prefix.
     */
    public static class LinkprTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return linkagePrefix;
        }
        
        @Override
        public String getKeyword()
        {
            return "LINKPR";
        }
    }
    
    /**
     * &COBOLNM| - COBOL name trigger.
     * Returns the COBOL-formatted name for current context.
     */
    public static class CobolNmTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            // Convert table name to COBOL format (replace underscores with hyphens)
            return currentTableName.replace("_", "-");
        }
        
        @Override
        public String getKeyword()
        {
            return "COBOLNM";
        }
    }
    
    /**
     * &COBOLWSNM| - COBOL working storage name trigger.
     * Returns working storage formatted name.
     */
    public static class CobolWsNmTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return "WS-" + currentTableName.replace("_", "-");
        }
        
        @Override
        public String getKeyword()
        {
            return "COBOLWSNM";
        }
    }
    
    // ============================================================
    // DATE/TIME TRIGGERS
    // ============================================================
    
    /**
     * &WDATE| - Working date trigger.
     * Returns current date in MM/DD/YYYY format.
     */
    public static class WDateTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        }
        
        @Override
        public String getKeyword()
        {
            return "WDATE";
        }
    }
    
    /**
     * &WTIME| - Working time trigger.
     * Returns current time in HH:MM:SS format.
     */
    public static class WTimeTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        
        @Override
        public String getKeyword()
        {
            return "WTIME";
        }
    }
    
    /**
     * &DATEINPFORMAT| - Date input format trigger.
     * Returns date format string for input validation.
     */
    public static class DateInpFormatTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return "YYYY-MM-DD";
        }
        
        @Override
        public String getKeyword()
        {
            return "DATEINPFORMAT";
        }
    }
    
    // ============================================================
    // VARIABLE TRIGGERS
    // ============================================================
    
    /**
     * &VAR1| through &VAR4| - Variable triggers.
     * If parameter provided, sets variable; otherwise returns value.
     */
    public static class VarTrigger implements Trigger
    {
        private final String varName;
        
        public VarTrigger(String varName)
        {
            this.varName = varName;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            if (params != null && params.length > 0 && !params[0].isEmpty())
            {
                // Set mode: &VAR1|value| sets the variable
                variables.put(varName, params[0]);
                return "";
            }
            else
            {
                // Get mode: &VAR1| returns the value
                return variables.getOrDefault(varName, "");
            }
        }
        
        @Override
        public String getKeyword()
        {
            return varName;
        }
    }
    
    // ============================================================
    // SQL TRIGGERS
    // ============================================================
    
    /**
     * &SQLC| - SQL columns trigger.
     * Generates SELECT column list for current table.
     */
    public static class SqlcTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            if (currentTableNbr == 0)
            {
                return "SELECT *";
            }
            
            try
            {
                EntityManager entityMgr = getEntityManager();
                TypedQuery<GenField> query = entityMgr.createQuery(
                    "SELECT f FROM GenField f WHERE f.tableIndexNbr = :tableNbr ORDER BY f.keyCctNbr",
                    GenField.class
                );
                query.setParameter("tableNbr", currentTableNbr);
                List<GenField> fields = query.getResultList();
                
                if (fields.isEmpty())
                {
                    return "SELECT *";
                }
                
                StringBuilder sb = new StringBuilder("SELECT ");
                for (int i = 0; i < fields.size(); i++)
                {
                    if (i > 0)
                    {
                        sb.append(",\n                       ");
                    }
                    sb.append("A.").append(fields.get(i).getColumnNm().trim());
                }
                return sb.toString();
            }
            catch (Exception e)
            {
                return "SELECT * /* Error: " + e.getMessage() + " */";
            }
        }
        
        @Override
        public String getKeyword()
        {
            return "SQLC";
        }
    }
    
    /**
     * &SQLF| - SQL fetch trigger.
     * Generates FETCH INTO statement for current table.
     */
    public static class SqlfTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            if (currentTableNbr == 0)
            {
                return "FETCH CURSOR INTO :HOST-VARS";
            }
            
            try
            {
                EntityManager entityMgr = getEntityManager();
                TypedQuery<GenField> query = entityMgr.createQuery(
                    "SELECT f FROM GenField f WHERE f.tableIndexNbr = :tableNbr ORDER BY f.keyCctNbr",
                    GenField.class
                );
                query.setParameter("tableNbr", currentTableNbr);
                List<GenField> fields = query.getResultList();
                
                if (fields.isEmpty())
                {
                    return "FETCH CURSOR INTO :HOST-VARS";
                }
                
                StringBuilder sb = new StringBuilder("FETCH CURSOR INTO\n");
                for (int i = 0; i < fields.size(); i++)
                {
                    if (i > 0)
                    {
                        sb.append(",\n");
                    }
                    sb.append("                       :");
                    sb.append(hostVarPrefix).append("-");
                    String cobolName = fields.get(i).getCobolNm();
                    if (cobolName != null && !cobolName.trim().isEmpty())
                    {
                        sb.append(cobolName.trim());
                    }
                    else
                    {
                        sb.append(fields.get(i).getColumnNm().trim().replace("_", "-"));
                    }
                }
                return sb.toString();
            }
            catch (Exception e)
            {
                return "FETCH CURSOR INTO :HOST-VARS /* Error: " + e.getMessage() + " */";
            }
        }
        
        @Override
        public String getKeyword()
        {
            return "SQLF";
        }
    }
    
    /**
     * &SQLI| - SQL insert trigger.
     * Generates INSERT statement for current table.
     */
    public static class SqliTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            if (currentTableNbr == 0)
            {
                return "INSERT INTO TABLE VALUES (:HOST-VARS)";
            }
            
            try
            {
                EntityManager entityMgr = getEntityManager();
                TypedQuery<GenField> query = entityMgr.createQuery(
                    "SELECT f FROM GenField f WHERE f.tableIndexNbr = :tableNbr ORDER BY f.keyCctNbr",
                    GenField.class
                );
                query.setParameter("tableNbr", currentTableNbr);
                List<GenField> fields = query.getResultList();
                
                if (fields.isEmpty())
                {
                    return "INSERT INTO " + currentTableName + " VALUES (:HOST-VARS)";
                }
                
                StringBuilder cols = new StringBuilder();
                StringBuilder vals = new StringBuilder();
                
                for (int i = 0; i < fields.size(); i++)
                {
                    if (i > 0)
                    {
                        cols.append(", ");
                        vals.append(", ");
                    }
                    String colName = fields.get(i).getColumnNm().trim();
                    cols.append(colName);
                    vals.append(":").append(hostVarPrefix).append("-");
                    String cobolName = fields.get(i).getCobolNm();
                    if (cobolName != null && !cobolName.trim().isEmpty())
                    {
                        vals.append(cobolName.trim());
                    }
                    else
                    {
                        vals.append(colName.replace("_", "-"));
                    }
                }
                
                return "INSERT INTO " + currentTableName + "\n" +
                       "       (" + cols + ")\n" +
                       "       VALUES\n" +
                       "       (" + vals + ")";
            }
            catch (Exception e)
            {
                return "INSERT INTO " + currentTableName + " VALUES (:HOST-VARS) /* Error */";
            }
        }
        
        @Override
        public String getKeyword()
        {
            return "SQLI";
        }
    }
    
    /**
     * &SQLINCL2| - SQL include level 2 trigger.
     */
    public static class SqlIncl2Trigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return "EXEC SQL INCLUDE SQLCA END-EXEC";
        }
        
        @Override
        public String getKeyword()
        {
            return "SQLINCL2";
        }
    }
    
    /**
     * &SQLINCL3| - SQL include level 3 trigger.
     */
    public static class SqlIncl3Trigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return "EXEC SQL INCLUDE " + currentTableName + " END-EXEC";
        }
        
        @Override
        public String getKeyword()
        {
            return "SQLINCL3";
        }
    }
    
    // ============================================================
    // KEY TRIGGERS
    // ============================================================
    
    /**
     * &FULLKEY| - Full key trigger.
     * Generates WHERE clause with all key columns.
     */
    public static class FullkeyTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            if (currentTableNbr == 0)
            {
                return "WHERE KEY_FIELD = :" + hostVarPrefix + "-KEY-FIELD";
            }
            
            try
            {
                EntityManager entityMgr = getEntityManager();
                // Query for key fields (keyFlagCd = 'Y')
                TypedQuery<GenField> query = entityMgr.createQuery(
                    "SELECT f FROM GenField f WHERE f.tableIndexNbr = :tableNbr " +
                    "AND f.keyFlagCd = 'Y' ORDER BY f.keyCctNbr",
                    GenField.class
                );
                query.setParameter("tableNbr", currentTableNbr);
                List<GenField> keyFields = query.getResultList();
                
                if (keyFields.isEmpty())
                {
                    return "WHERE 1=1 /* No key fields defined */";
                }
                
                StringBuilder sb = new StringBuilder("WHERE ");
                for (int i = 0; i < keyFields.size(); i++)
                {
                    if (i > 0)
                    {
                        sb.append("\n                AND ");
                    }
                    String colName = keyFields.get(i).getColumnNm().trim();
                    String cobolName = keyFields.get(i).getCobolNm();
                    if (cobolName == null || cobolName.trim().isEmpty())
                    {
                        cobolName = colName.replace("_", "-");
                    }
                    else
                    {
                        cobolName = cobolName.trim();
                    }
                    sb.append(colName).append(" = :").append(hostVarPrefix).append("-").append(cobolName);
                }
                return sb.toString();
            }
            catch (Exception e)
            {
                return "WHERE KEY_FIELD = :" + hostVarPrefix + "-KEY-FIELD /* Error */";
            }
        }
        
        @Override
        public String getKeyword()
        {
            return "FULLKEY";
        }
    }
    
    /**
     * &SETKEY| - Set key trigger.
     * Generates MOVE statements for key fields.
     */
    public static class SetkeyTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            if (currentTableNbr == 0)
            {
                return "           MOVE " + linkagePrefix + "-KEY-FIELD TO " + hostVarPrefix + "-KEY-FIELD";
            }
            
            try
            {
                EntityManager entityMgr = getEntityManager();
                TypedQuery<GenField> query = entityMgr.createQuery(
                    "SELECT f FROM GenField f WHERE f.tableIndexNbr = :tableNbr " +
                    "AND f.keyFlagCd = 'Y' ORDER BY f.keyCctNbr",
                    GenField.class
                );
                query.setParameter("tableNbr", currentTableNbr);
                List<GenField> keyFields = query.getResultList();
                
                if (keyFields.isEmpty())
                {
                    return "      * No key fields defined";
                }
                
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < keyFields.size(); i++)
                {
                    if (i > 0)
                    {
                        sb.append("\n");
                    }
                    String cobolName = keyFields.get(i).getCobolNm();
                    if (cobolName == null || cobolName.trim().isEmpty())
                    {
                        cobolName = keyFields.get(i).getColumnNm().trim().replace("_", "-");
                    }
                    else
                    {
                        cobolName = cobolName.trim();
                    }
                    sb.append("           MOVE ").append(linkagePrefix).append("-").append(cobolName)
                      .append(" TO ").append(hostVarPrefix).append("-").append(cobolName);
                }
                return sb.toString();
            }
            catch (Exception e)
            {
                return "      * Error generating key moves: " + e.getMessage();
            }
        }
        
        @Override
        public String getKeyword()
        {
            return "SETKEY";
        }
    }
    
    /**
     * &BUILDSTDKEY| - Build standard key trigger.
     * Generates standard key building logic.
     */
    public static class BuildStdKeyTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("      * Build Standard Key\n");
            sb.append("           MOVE SPACES TO WS-KEY-AREA\n");
            sb.append("           STRING\n");
            sb.append("               WS-COMPANY-CD DELIMITED SIZE\n");
            sb.append("               WS-POLICY-NBR DELIMITED SIZE\n");
            sb.append("               INTO WS-KEY-AREA");
            return sb.toString();
        }
        
        @Override
        public String getKeyword()
        {
            return "BUILDSTDKEY";
        }
    }
    
    /**
     * &UPDATEKEY| - Update key trigger.
     * Generates key update logic.
     */
    public static class UpdateKeyTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("      * Update Key Values\n");
            sb.append("           ADD 1 TO WS-KEY-SEQ");
            return sb.toString();
        }
        
        @Override
        public String getKeyword()
        {
            return "UPDATEKEY";
        }
    }
    
    /**
     * &KEYID| - Key ID trigger.
     * Returns key identifier.
     */
    public static class KeyIdTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return "KEY-" + currentTableName.replace("_", "-");
        }
        
        @Override
        public String getKeyword()
        {
            return "KEYID";
        }
    }
    
    // ============================================================
    // HOST VARIABLE TRIGGERS  
    // ============================================================
    
    /**
     * &HOST_| - Host variable prefix trigger.
     * Returns the host variable prefix with colon.
     */
    public static class HostTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return ":" + hostVarPrefix + "-";
        }
        
        @Override
        public String getKeyword()
        {
            return "HOST_";
        }
    }
    
    /**
     * &MOVEHV2| - Move host variable level 2 trigger.
     * Generates MOVE statements for host variables.
     */
    public static class Movehv2Trigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            if (currentTableNbr == 0)
            {
                return "           MOVE SPACES TO " + hostVarPrefix + "-DATA";
            }
            
            try
            {
                EntityManager entityMgr = getEntityManager();
                TypedQuery<GenField> query = entityMgr.createQuery(
                    "SELECT f FROM GenField f WHERE f.tableIndexNbr = :tableNbr ORDER BY f.keyCctNbr",
                    GenField.class
                );
                query.setParameter("tableNbr", currentTableNbr);
                List<GenField> fields = query.getResultList();
                
                if (fields.isEmpty())
                {
                    return "           MOVE SPACES TO " + hostVarPrefix + "-DATA";
                }
                
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < fields.size(); i++)
                {
                    if (i > 0)
                    {
                        sb.append("\n");
                    }
                    GenField field = fields.get(i);
                    String cobolName = field.getCobolNm();
                    if (cobolName == null || cobolName.trim().isEmpty())
                    {
                        cobolName = field.getColumnNm().trim().replace("_", "-");
                    }
                    else
                    {
                        cobolName = cobolName.trim();
                    }
                    
                    String dataType = field.getDataTypeNm();
                    // S=SmallInt, I=Integer, D=Decimal, F=Float are numeric
                    if (dataType != null && ("S".equals(dataType) || "I".equals(dataType) || "D".equals(dataType) || "F".equals(dataType)))
                    {
                        sb.append("           MOVE ZEROS TO ").append(hostVarPrefix).append("-").append(cobolName);
                    }
                    else
                    {
                        sb.append("           MOVE SPACES TO ").append(hostVarPrefix).append("-").append(cobolName);
                    }
                }
                return sb.toString();
            }
            catch (Exception e)
            {
                return "      * Error generating host var moves: " + e.getMessage();
            }
        }
        
        @Override
        public String getKeyword()
        {
            return "MOVEHV2";
        }
    }
    
    // ============================================================
    // WORKING STORAGE TRIGGERS
    // ============================================================
    
    /**
     * &WSVARS| - Working storage variables trigger.
     * Generates working storage variable definitions.
     */
    public static class WsvarsTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("       01  WS-WORKING-STORAGE.\n");
            sb.append("           05  WS-FIRST-TIME-SW     PIC X(1) VALUE 'Y'.\n");
            sb.append("           05  WS-DEBUG-PARAGRAPH   PIC X(30).\n");
            sb.append("           05  WS-DEBUG-SQLCODE     PIC S9(9) COMP.\n");
            sb.append("           05  WS-DEBUG-COMMENT     PIC X(50).\n");
            sb.append("           05  WS-RETURN-CODE       PIC X(2).\n");
            sb.append("           05  WS-EOF-SW            PIC X(1) VALUE 'N'.\n");
            sb.append("               88  WS-EOF           VALUE 'Y'.\n");
            sb.append("               88  WS-NOT-EOF       VALUE 'N'.\n");
            sb.append("           05  WS-ERROR-SW          PIC X(1) VALUE 'N'.\n");
            sb.append("               88  WS-ERROR         VALUE 'Y'.\n");
            sb.append("               88  WS-NO-ERROR      VALUE 'N'.");
            return sb.toString();
        }
        
        @Override
        public String getKeyword()
        {
            return "WSVARS";
        }
    }
    
    /**
     * &WORKSTATION| - Workstation variables trigger.
     * Generates workstation-specific variables.
     */
    public static class WorkstationTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("       01  WS-WORKSTATION-VARS.\n");
            sb.append("           05  WS-TERMINAL-ID       PIC X(4).\n");
            sb.append("           05  WS-OPERATOR-ID       PIC X(8).\n");
            sb.append("           05  WS-TRANSACTION-ID    PIC X(4).");
            return sb.toString();
        }
        
        @Override
        public String getKeyword()
        {
            return "WORKSTATION";
        }
    }
    
    // ============================================================
    // IO MODULE TRIGGERS
    // ============================================================
    
    /**
     * &IOMODULE| - IO module trigger.
     * Generates IO module name.
     */
    public static class IoModuleTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return programName + "IO";
        }
        
        @Override
        public String getKeyword()
        {
            return "IOMODULE";
        }
    }
    
    /**
     * &IOMODULECOPYBOOKS| - IO module copybooks trigger.
     * Generates COPY statements for IO module.
     */
    public static class IoModuleCopybooksTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("           COPY " + programName + "WS.\n");
            sb.append("           COPY " + programName + "LK.\n");
            sb.append("           COPY SQLCA.");
            return sb.toString();
        }
        
        @Override
        public String getKeyword()
        {
            return "IOMODULECOPYBOOKS";
        }
    }
    
    /**
     * &IOPARAGRAPHS| - IO paragraphs trigger.
     * Generates standard IO paragraph shells.
     */
    public static class IoParagraphsTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("       READ-" + currentTableName.replace("_", "-") + ".\n");
            sb.append("           PERFORM OPEN-CURSOR-" + currentTableName.replace("_", "-") + "\n");
            sb.append("           PERFORM FETCH-" + currentTableName.replace("_", "-") + "\n");
            sb.append("           .\n");
            sb.append("       OPEN-CURSOR-" + currentTableName.replace("_", "-") + ".\n");
            sb.append("           EXEC SQL\n");
            sb.append("               OPEN CURSOR-" + currentTableName.replace("_", "-") + "\n");
            sb.append("           END-EXEC\n");
            sb.append("           .\n");
            sb.append("       FETCH-" + currentTableName.replace("_", "-") + ".\n");
            sb.append("           EXEC SQL\n");
            sb.append("               FETCH CURSOR-" + currentTableName.replace("_", "-") + "\n");
            sb.append("               INTO :HOST-VARS\n");
            sb.append("           END-EXEC\n");
            sb.append("           .");
            return sb.toString();
        }
        
        @Override
        public String getKeyword()
        {
            return "IOPARAGRAPHS";
        }
    }
    
    // ============================================================
    // RATING TRIGGERS
    // ============================================================
    
    /**
     * &RATEKEYDEF| - Rate key definition trigger.
     * Generates working storage for rate keys.
     */
    public static class RateKeyDefTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("      * Rate Key Working Storage Definitions\n");
            sb.append("       01  WS-RATE-KEY-DATA.\n");
            sb.append("           05  WS-RATE-KEY-ID       PIC X(10).\n");
            sb.append("           05  WS-RATE-KEY-DESC     PIC X(30).\n");
            sb.append("           05  WS-RATE-KEY-VALUE    PIC X(20).");
            return sb.toString();
        }
        
        @Override
        public String getKeyword()
        {
            return "RATEKEYDEF";
        }
    }
    
    /**
     * &RATEELEDEF| - Rate element definition trigger.
     * Generates working storage for rate elements.
     */
    public static class RateEleDefTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("      * Rate Element Working Storage Definitions\n");
            sb.append("       01  WS-RATE-ELEMENT-DATA.\n");
            sb.append("           05  WS-RATE-ELE-ID       PIC X(10).\n");
            sb.append("           05  WS-RATE-ELE-DESC     PIC X(30).\n");
            sb.append("           05  WS-RATE-ELE-VALUE    PIC S9(9)V99 COMP-3.");
            return sb.toString();
        }
        
        @Override
        public String getKeyword()
        {
            return "RATEELEDEF";
        }
    }
    
    /**
     * &ELEDESC| - Element description trigger.
     * Returns element description.
     */
    public static class EleDescTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            if (params != null && params.length > 0)
            {
                return params[0];
            }
            return "ELEMENT-DESC";
        }
        
        @Override
        public String getKeyword()
        {
            return "ELEDESC";
        }
    }
    
    // ============================================================
    // CONDITIONAL TRIGGERS
    // ============================================================
    
    // Conditional state tracking
    private static boolean conditionalActive = false;
    private static boolean conditionalTrue = false;
    private static boolean inElseBlock = false;
    
    /**
     * &<|prefix| - Include trigger.
     * Includes line content if prefix matches current linkage prefix.
     */
    public static class IncludeTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            if (params == null || params.length == 0)
            {
                return "";
            }
            
            String expectedPrefix = params[0].trim();
            if (linkagePrefix.equals(expectedPrefix))
            {
                // Return rest of parameters as content
                if (params.length > 1)
                {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < params.length; i++)
                    {
                        if (i > 1) sb.append("|");
                        sb.append(params[i]);
                    }
                    return sb.toString();
                }
                return "";
            }
            return "";  // Skip line - prefix doesn't match
        }
        
        @Override
        public String getKeyword()
        {
            return "<";
        }
    }
    
    /**
     * &!|prefix| - Exclude trigger.
     * Excludes line content if prefix matches current linkage prefix.
     */
    public static class ExcludeTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            if (params == null || params.length == 0)
            {
                return "";
            }
            
            String excludePrefix = params[0].trim();
            if (!linkagePrefix.equals(excludePrefix))
            {
                // Return rest of parameters as content (not excluded)
                if (params.length > 1)
                {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < params.length; i++)
                    {
                        if (i > 1) sb.append("|");
                        sb.append(params[i]);
                    }
                    return sb.toString();
                }
                return "";
            }
            return "";  // Exclude - prefix matches
        }
        
        @Override
        public String getKeyword()
        {
            return "!";
        }
    }
    
    /**
     * &IF|condition| - If trigger.
     * Generates COBOL IF statement.
     */
    public static class IfTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            String condition = (params != null && params.length > 0) ? params[0] : "TRUE";
            return "           IF " + condition;
        }
        
        @Override
        public String getKeyword()
        {
            return "IF";
        }
    }
    
    /**
     * &ELSE| - Else trigger.
     * Generates COBOL ELSE statement.
     */
    public static class ElseTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return "           ELSE";
        }
        
        @Override
        public String getKeyword()
        {
            return "ELSE";
        }
    }
    
    /**
     * &ENDIF| - End if trigger.
     * Generates COBOL END-IF statement.
     */
    public static class EndIfTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return "           END-IF";
        }
        
        @Override
        public String getKeyword()
        {
            return "ENDIF";
        }
    }
    
    /**
     * &IFINSLINE|code| - If insurance line trigger.
     * Conditional based on insurance line code.
     */
    public static class IfInsLineTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            if (params == null || params.length == 0)
            {
                return "";
            }
            
            String expectedLine = params[0].trim();
            if (insuranceLineCd.equals(expectedLine))
            {
                // Return content after the condition
                if (params.length > 1)
                {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < params.length; i++)
                    {
                        if (i > 1) sb.append("|");
                        sb.append(params[i]);
                    }
                    return sb.toString();
                }
            }
            return "";  // Skip if insurance line doesn't match
        }
        
        @Override
        public String getKeyword()
        {
            return "IFINSLINE";
        }
    }
    
    // ============================================================
    // CODE GENERATION TRIGGERS
    // ============================================================
    
    /**
     * &MOVE|source|dest| - Move trigger.
     * Generates COBOL MOVE statement.
     */
    public static class MoveTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            if (params == null || params.length < 2)
            {
                return "           MOVE SOURCE TO DEST";
            }
            return "           MOVE " + params[0] + " TO " + params[1];
        }
        
        @Override
        public String getKeyword()
        {
            return "MOVE";
        }
    }
    
    /**
     * &PERFORM|paragraph| - Perform trigger.
     * Generates COBOL PERFORM statement.
     */
    public static class PerformTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            if (params == null || params.length == 0)
            {
                return "           PERFORM PARAGRAPH-NAME";
            }
            return "           PERFORM " + params[0];
        }
        
        @Override
        public String getKeyword()
        {
            return "PERFORM";
        }
    }
    
    /**
     * &CODE|code| - Code trigger.
     * Outputs raw COBOL code.
     */
    public static class CodeTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            if (params == null || params.length == 0)
            {
                return "";
            }
            // Join all parameters with pipe (in case code had pipes)
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < params.length; i++)
            {
                if (i > 0) sb.append("|");
                sb.append(params[i]);
            }
            return sb.toString();
        }
        
        @Override
        public String getKeyword()
        {
            return "CODE";
        }
    }
    
    /**
     * &CMNT|text| - Comment trigger.
     * Generates COBOL comment line.
     */
    public static class CmntTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            String comment = (params != null && params.length > 0) ? params[0] : "";
            return "      * " + comment;
        }
        
        @Override
        public String getKeyword()
        {
            return "CMNT";
        }
    }
    
    /**
     * &TEMPLATE| - Template trigger.
     * Processes external template (placeholder for now).
     */
    public static class TemplateTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            // TODO: Implement external template inclusion
            return "      * TODO: Process external template";
        }
        
        @Override
        public String getKeyword()
        {
            return "TEMPLATE";
        }
    }
    
    /**
     * &SKIP| - Skip trigger.
     * Skips the current line (outputs nothing).
     */
    public static class SkipTrigger implements Trigger
    {
        @Override
        public boolean replacesLine()
        {
            return true;
        }
        
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return "";  // Skip - output nothing
        }
        
        @Override
        public String getKeyword()
        {
            return "SKIP";
        }
    }
    
    // ============================================================
    // CICS/BATCH TRIGGERS
    // ============================================================
    
    /**
     * &CICS_| - CICS trigger.
     * Returns CICS-specific prefix or code.
     */
    public static class CicsTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return "EXEC CICS ";
        }
        
        @Override
        public String getKeyword()
        {
            return "CICS_";
        }
    }
    
    /**
     * &BATCH| - Batch trigger.
     * Returns batch-specific indicator.
     */
    public static class BatchTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return "BATCH";
        }
        
        @Override
        public String getKeyword()
        {
            return "BATCH";
        }
    }
    
    // ============================================================
    // OTHER TRIGGERS
    // ============================================================
    
    /**
     * &QUOTESEQNBR| - Quote sequence number trigger.
     * Returns quote sequence number field.
     */
    public static class QuoteSequenceNbrTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return "QUOTE-SEQ-NBR";
        }
        
        @Override
        public String getKeyword()
        {
            return "QUOTESEQNBR";
        }
    }
    
    /**
     * &DELROWSIND| - Delete rows indicator trigger.
     * Returns delete rows indicator field.
     */
    public static class DelRowsIndTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return "WS-DELETE-ROWS-IND";
        }
        
        @Override
        public String getKeyword()
        {
            return "DELROWSIND";
        }
    }
    
    /**
     * &SECURAASOFWHERE| - Secura as-of date WHERE clause trigger.
     * Generates WHERE clause for as-of date processing.
     */
    public static class SecuraAsOfWhereTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return "AND EFF_DT <= :" + hostVarPrefix + "-AS-OF-DT " +
                   "AND EXP_DT > :" + hostVarPrefix + "-AS-OF-DT";
        }
        
        @Override
        public String getKeyword()
        {
            return "SECURAASOFWHERE";
        }
    }
    
    /**
     * &WCADT| - WC ADT trigger.
     * Workers Comp ADT-specific processing.
     */
    public static class WcAdtTrigger implements Trigger
    {
        @Override
        public String process(GenCore genCore, String[] params)
        {
            return "WC-ADT";
        }
        
        @Override
        public String getKeyword()
        {
            return "WCADT";
        }
    }
}
