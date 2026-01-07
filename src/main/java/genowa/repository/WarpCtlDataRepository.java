package genowa.repository;

import genowa.model.WarpCtlData;
import genowa.model.WarpCtlDataId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository for WARP_CTL_DATA operations.
 * Provides methods to query and persist Element Value mappings.
 */
public class WarpCtlDataRepository {

    private final EntityManager em;

    public WarpCtlDataRepository(EntityManager em) {
        this.em = em;
    }

    /**
     * Find by composite primary key.
     */
    public Optional<WarpCtlData> findById(WarpCtlDataId id) {
        return Optional.ofNullable(em.find(WarpCtlData.class, id));
    }

    /**
     * Find all mappings for an insurance line.
     */
    public List<WarpCtlData> findByInsuranceLine(String insuranceLineCode) {
        TypedQuery<WarpCtlData> query = em.createQuery(
            "SELECT w FROM WarpCtlData w WHERE w.id.insuranceLineCode = :lineCode " +
            "ORDER BY w.id.levelTypeCode, w.id.tableName, w.id.controlCode",
            WarpCtlData.class);
        query.setParameter("lineCode", insuranceLineCode);
        return query.getResultList();
    }

    /**
     * Find all mappings for a specific table within an insurance line.
     */
    public List<WarpCtlData> findByInsuranceLineAndTable(String insuranceLineCode, 
                                                          String tableName) {
        TypedQuery<WarpCtlData> query = em.createQuery(
            "SELECT w FROM WarpCtlData w " +
            "WHERE w.id.insuranceLineCode = :lineCode " +
            "AND w.id.tableName = :tableName " +
            "ORDER BY w.id.controlCode",
            WarpCtlData.class);
        query.setParameter("lineCode", insuranceLineCode);
        query.setParameter("tableName", tableName);
        return query.getResultList();
    }
    
    /**
     * Find all mappings for a specific table (with full key).
     */
    public List<WarpCtlData> findByTable(String insuranceLineCode, 
                                          genowa.model.LevelType levelType, 
                                          String tableName) {
        TypedQuery<WarpCtlData> query = em.createQuery(
            "SELECT w FROM WarpCtlData w " +
            "WHERE w.id.insuranceLineCode = :lineCode " +
            "AND w.id.levelTypeCode = :levelType " +
            "AND w.id.tableName = :tableName " +
            "ORDER BY w.id.controlCode",
            WarpCtlData.class);
        query.setParameter("lineCode", insuranceLineCode);
        query.setParameter("levelType", levelType.getCode());
        query.setParameter("tableName", tableName);
        return query.getResultList();
    }

    /**
     * Find all mappings for a level type within an insurance line.
     */
    public List<WarpCtlData> findByInsuranceLineAndLevelType(String insuranceLineCode,
                                                              String levelTypeCode) {
        TypedQuery<WarpCtlData> query = em.createQuery(
            "SELECT w FROM WarpCtlData w " +
            "WHERE w.id.insuranceLineCode = :lineCode " +
            "AND w.id.levelTypeCode = :levelType " +
            "ORDER BY w.id.tableName, w.id.controlCode",
            WarpCtlData.class);
        query.setParameter("lineCode", insuranceLineCode);
        query.setParameter("levelType", levelTypeCode);
        return query.getResultList();
    }

    /**
     * Find all distinct insurance lines that have configurations.
     */
    public List<String> findDistinctInsuranceLines() {
        TypedQuery<String> query = em.createQuery(
            "SELECT DISTINCT w.id.insuranceLineCode FROM WarpCtlData w " +
            "ORDER BY w.id.insuranceLineCode",
            String.class);
        return query.getResultList();
    }

    /**
     * Find all tables configured for an insurance line and level type.
     */
    public List<String> findTablesByInsuranceLineAndLevelType(String insuranceLineCode,
                                                               String levelTypeCode) {
        TypedQuery<String> query = em.createQuery(
            "SELECT DISTINCT w.id.tableName FROM WarpCtlData w " +
            "WHERE w.id.insuranceLineCode = :lineCode " +
            "AND w.id.levelTypeCode = :levelType " +
            "ORDER BY w.id.tableName",
            String.class);
        query.setParameter("lineCode", insuranceLineCode);
        query.setParameter("levelType", levelTypeCode);
        return query.getResultList();
    }

    /**
     * Save or update a mapping.
     */
    public WarpCtlData save(WarpCtlData data) {
        if (em.find(WarpCtlData.class, data.getId()) == null) {
            em.persist(data);
            return data;
        } else {
            return em.merge(data);
        }
    }

    /**
     * Save multiple mappings in a batch.
     */
    public void saveAll(List<WarpCtlData> dataList) {
        for (WarpCtlData data : dataList) {
            save(data);
        }
    }

    /**
     * Delete a mapping.
     */
    public void delete(WarpCtlData data) {
        em.remove(em.contains(data) ? data : em.merge(data));
    }

    /**
     * Delete all mappings for a table within an insurance line.
     */
    public int deleteByInsuranceLineAndTable(String insuranceLineCode, String tableName) {
        return em.createQuery(
            "DELETE FROM WarpCtlData w " +
            "WHERE w.id.insuranceLineCode = :lineCode " +
            "AND w.id.tableName = :tableName")
            .setParameter("lineCode", insuranceLineCode)
            .setParameter("tableName", tableName)
            .executeUpdate();
    }
}
