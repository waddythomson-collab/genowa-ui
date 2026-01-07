package genowa;

import genowa.model.*;
import genowa.repository.WarpCtlDataRepository;
import genowa.repository.WarpCtlRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Central database access singleton.
 * Manages the JPA EntityManagerFactory and provides repositories.
 */
public class Database {
    
    private static Database instance;
    private EntityManagerFactory emf;
    
    private Database() {
        // Initialize on first access
    }
    
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
    
    /**
     * Initialize the database connection.
     * Call this once at application startup.
     */
    public void initialize() {
        if (emf == null || !emf.isOpen()) {
            System.out.println("Initializing database connection...");
            emf = Persistence.createEntityManagerFactory("genowa");
            System.out.println("Database connection established.");
            
            // Insert sample data if database is empty
            // insertSampleDataIfEmpty(); // Disabled - using real data
        }
    }
    
    /**
     * Shutdown the database connection.
     * Call this when application closes.
     */
    public void shutdown() {
        if (emf != null && emf.isOpen()) {
            System.out.println("Closing database connection...");
            emf.close();
            System.out.println("Database connection closed.");
        }
    }
    
    /**
     * Get a new EntityManager.
     * Caller is responsible for closing it.
     */
    public EntityManager createEntityManager() {
        if (emf == null || !emf.isOpen()) {
            initialize();
        }
        return emf.createEntityManager();
    }
    
    /**
     * Execute a read-only operation.
     */
    public <T> T executeRead(Function<EntityManager, T> operation) {
        EntityManager em = createEntityManager();
        try {
            return operation.apply(em);
        } finally {
            em.close();
        }
    }
    
    /**
     * Execute a transactional write operation.
     */
    public void executeWrite(Consumer<EntityManager> operation) {
        EntityManager em = createEntityManager();
        try {
            em.getTransaction().begin();
            operation.accept(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    /**
     * Execute a transactional write operation with return value.
     */
    public <T> T executeWriteWithResult(Function<EntityManager, T> operation) {
        EntityManager em = createEntityManager();
        try {
            em.getTransaction().begin();
            T result = operation.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    // ===== Repository access methods =====
    
    /**
     * Get WarpCtl entries by insurance line.
     */
    public List<WarpCtl> getWarpCtlByInsuranceLine(String insuranceLineCode) {
        return executeRead(em -> {
            WarpCtlRepository repo = new WarpCtlRepository(em);
            return repo.findByInsuranceLine(insuranceLineCode);
        });
    }
    
    /**
     * Get WarpCtl entries by insurance line and level type.
     */
    public List<WarpCtl> getWarpCtlByInsuranceLineAndLevelType(String insuranceLineCode, LevelType levelType) {
        return executeRead(em -> {
            WarpCtlRepository repo = new WarpCtlRepository(em);
            return repo.findByInsuranceLineAndLevelType(insuranceLineCode, levelType);
        });
    }
    
    /**
     * Get all distinct insurance lines.
     */
    public List<String> getAllInsuranceLines() {
        return executeRead(em -> {
            WarpCtlRepository repo = new WarpCtlRepository(em);
            return repo.findAllInsuranceLines();
        });
    }
    
    /**
     * Save a WarpCtl entry.
     */
    public WarpCtl saveWarpCtl(WarpCtl warpCtl) {
        return executeWriteWithResult(em -> {
            WarpCtlRepository repo = new WarpCtlRepository(em);
            return repo.save(warpCtl);
        });
    }
    
    /**
     * Delete a WarpCtl entry.
     */
    public void deleteWarpCtl(WarpCtl warpCtl) {
        executeWrite(em -> {
            WarpCtlRepository repo = new WarpCtlRepository(em);
            WarpCtl managed = em.find(WarpCtl.class, warpCtl.getId());
            if (managed != null) {
                repo.delete(managed);
            }
        });
    }
    
    /**
     * Get WarpCtlData (element mappings) for a table.
     */
    public List<WarpCtlData> getWarpCtlData(String insuranceLineCode, LevelType levelType, String tableName) {
        return executeRead(em -> {
            WarpCtlDataRepository repo = new WarpCtlDataRepository(em);
            return repo.findByTable(insuranceLineCode, levelType, tableName);
        });
    }
    
    /**
     * Save a WarpCtlData entry.
     */
    public WarpCtlData saveWarpCtlData(WarpCtlData data) {
        return executeWriteWithResult(em -> {
            WarpCtlDataRepository repo = new WarpCtlDataRepository(em);
            return repo.save(data);
        });
    }
    
    // ===== Sample data initialization =====
    
    private void insertSampleDataIfEmpty() {
        List<String> lines = getAllInsuranceLines();
        if (lines.isEmpty()) {
            System.out.println("Database is empty - inserting sample data...");
            insertSampleData();
            System.out.println("Sample data inserted.");
        } else {
            System.out.println("Database contains " + lines.size() + " insurance line(s).");
        }
    }
    
    private void insertSampleData() {
        executeWrite(em -> {
            WarpCtlRepository repo = new WarpCtlRepository(em);
            WarpCtlDataRepository dataRepo = new WarpCtlDataRepository(em);
            
            // Personal Auto - Premium tables
            WarpCtl paPremium = new WarpCtl("PA", LevelType.PREMIUM, "PA_PREMIUM_V");
            paPremium.setLevel(1);
            paPremium.setSubLevel(0);
            repo.save(paPremium);
            
            WarpCtl paPrmDetail = new WarpCtl("PA", LevelType.PREMIUM, "PA_PRM_DETAIL_V");
            paPrmDetail.setLevel(2);
            paPrmDetail.setSubLevel(0);
            paPrmDetail.setParentTableName("PA_PREMIUM_V");
            repo.save(paPrmDetail);
            
            // Personal Auto - Form tables
            WarpCtl paForms = new WarpCtl("PA", LevelType.FORM, "PA_FORMS_V");
            paForms.setLevel(1);
            paForms.setSubLevel(0);
            repo.save(paForms);
            
            // Personal Auto - Primary tables
            WarpCtl paVehicle = new WarpCtl("PA", LevelType.PRIMARY, "PA_VEHICLE_V");
            paVehicle.setLevel(1);
            paVehicle.setSubLevel(0);
            repo.save(paVehicle);
            
            WarpCtl paVehCov = new WarpCtl("PA", LevelType.PRIMARY, "PA_VEH_COVERAGE_V");
            paVehCov.setLevel(2);
            paVehCov.setSubLevel(0);
            paVehCov.setParentTableName("PA_VEHICLE_V");
            repo.save(paVehCov);
            
            WarpCtl paDriver = new WarpCtl("PA", LevelType.PRIMARY, "PA_DRIVER_V");
            paDriver.setLevel(1);
            paDriver.setSubLevel(1);
            repo.save(paDriver);
            
            // Personal Auto - Coverage tables
            WarpCtl paCovDef = new WarpCtl("PA", LevelType.COVERAGE, "PA_COVERAGE_DEF_V");
            paCovDef.setLevel(1);
            paCovDef.setSubLevel(0);
            repo.save(paCovDef);
            
            // Personal Auto - Support tables
            WarpCtl paSupport = new WarpCtl("PA", LevelType.SUPPORT, "PA_SUPPORT_V");
            paSupport.setLevel(1);
            paSupport.setSubLevel(0);
            repo.save(paSupport);
            
            // Add element mappings for PA_PREMIUM_V
            addElementMapping(dataRepo, paPremium, ElementValue.EV_02, "VEH_UNIT_NBR");
            addElementMapping(dataRepo, paPremium, ElementValue.EV_03, "COV_SEQ_NBR");
            addElementMapping(dataRepo, paPremium, ElementValue.EV_04, "COV_CD");
            addElementMapping(dataRepo, paPremium, ElementValue.EV_05, "INS_LINE_CD");
            addElementMapping(dataRepo, paPremium, ElementValue.EV_06, "PRM_OVERRIDE_IND");
            addElementMapping(dataRepo, paPremium, ElementValue.EV_07, "PREMIUM_AMT");
            addElementMapping(dataRepo, paPremium, ElementValue.EV_08, "TRANS_PRM_AMT");
            
            // Homeowners line (minimal)
            WarpCtl hoPremium = new WarpCtl("HO", LevelType.PREMIUM, "HO_PREMIUM_V");
            hoPremium.setLevel(1);
            hoPremium.setSubLevel(0);
            repo.save(hoPremium);
            
            WarpCtl hoCoverage = new WarpCtl("HO", LevelType.COVERAGE, "HO_COVERAGE_V");
            hoCoverage.setLevel(1);
            hoCoverage.setSubLevel(0);
            repo.save(hoCoverage);
            
            // Commercial Auto (minimal)
            WarpCtl caPremium = new WarpCtl("CA", LevelType.PREMIUM, "CA_PREMIUM_V");
            caPremium.setLevel(1);
            caPremium.setSubLevel(0);
            repo.save(caPremium);
        });
    }
    
    private void addElementMapping(WarpCtlDataRepository repo, WarpCtl parent, 
                                    ElementValue elementValue, String columnName) {
        WarpCtlData data = new WarpCtlData();
        data.setId(new WarpCtlDataId(
            parent.getInsuranceLineCode(),
            parent.getLevelType(),
            parent.getTableName(),
            elementValue
        ));
        data.setColumnName(columnName);
        data.setWarpCtl(parent);
        repo.save(data);
    }
}
