package genowa.repository;

import genowa.model.LevelType;
import genowa.model.WarpCtl;
import genowa.model.WarpCtlId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository for WARP_CTL table access.
 * Manages table hierarchy assignments per Insurance Line.
 */
public class WarpCtlRepository {
    
    private final EntityManager em;
    
    public WarpCtlRepository(EntityManager em) {
        this.em = em;
    }
    
    /**
     * Find by composite primary key.
     */
    public Optional<WarpCtl> findById(WarpCtlId id) {
        return Optional.ofNullable(em.find(WarpCtl.class, id));
    }
    
    /**
     * Find by composite key components.
     */
    public Optional<WarpCtl> findById(String insuranceLineCode, LevelType levelType, String tableName) {
        return findById(new WarpCtlId(insuranceLineCode, levelType, tableName));
    }
    
    /**
     * Get all table assignments for an Insurance Line.
     * Returns tables across all level types.
     */
    public List<WarpCtl> findByInsuranceLine(String insuranceLineCode) {
        TypedQuery<WarpCtl> query = em.createQuery(
            "SELECT w FROM WarpCtl w WHERE w.id.insuranceLineCode = :lineCode " +
            "ORDER BY w.id.levelType, w.level, w.subLevel",
            WarpCtl.class);
        query.setParameter("lineCode", insuranceLineCode);
        return query.getResultList();
    }
    
    /**
     * Get table assignments for a specific Insurance Line and Level Type.
     * This is what populates the tree view for one category (e.g., Premium tables).
     */
    public List<WarpCtl> findByInsuranceLineAndLevelType(String insuranceLineCode, LevelType levelType) {
        TypedQuery<WarpCtl> query = em.createQuery(
            "SELECT w FROM WarpCtl w WHERE w.id.insuranceLineCode = :lineCode " +
            "AND w.id.levelType = :levelType " +
            "ORDER BY w.level, w.subLevel",
            WarpCtl.class);
        query.setParameter("lineCode", insuranceLineCode);
        query.setParameter("levelType", levelType);
        return query.getResultList();
    }
    
    /**
     * Get root tables (no parent) for an Insurance Line and Level Type.
     */
    public List<WarpCtl> findRootTables(String insuranceLineCode, LevelType levelType) {
        TypedQuery<WarpCtl> query = em.createQuery(
            "SELECT w FROM WarpCtl w WHERE w.id.insuranceLineCode = :lineCode " +
            "AND w.id.levelType = :levelType " +
            "AND (w.parentTableName IS NULL OR w.parentTableName = '') " +
            "ORDER BY w.level, w.subLevel",
            WarpCtl.class);
        query.setParameter("lineCode", insuranceLineCode);
        query.setParameter("levelType", levelType);
        return query.getResultList();
    }
    
    /**
     * Get child tables of a specific parent.
     */
    public List<WarpCtl> findChildren(String insuranceLineCode, LevelType levelType, String parentTableName) {
        TypedQuery<WarpCtl> query = em.createQuery(
            "SELECT w FROM WarpCtl w WHERE w.id.insuranceLineCode = :lineCode " +
            "AND w.id.levelType = :levelType " +
            "AND w.parentTableName = :parentName " +
            "ORDER BY w.subLevel",
            WarpCtl.class);
        query.setParameter("lineCode", insuranceLineCode);
        query.setParameter("levelType", levelType);
        query.setParameter("parentName", parentTableName);
        return query.getResultList();
    }
    
    /**
     * Get all distinct Insurance Lines that have table assignments.
     */
    public List<String> findAllInsuranceLines() {
        TypedQuery<String> query = em.createQuery(
            "SELECT DISTINCT w.id.insuranceLineCode FROM WarpCtl w ORDER BY w.id.insuranceLineCode",
            String.class);
        return query.getResultList();
    }
    
    /**
     * Get all Level Types used by a specific Insurance Line.
     */
    public List<LevelType> findLevelTypesByInsuranceLine(String insuranceLineCode) {
        TypedQuery<LevelType> query = em.createQuery(
            "SELECT DISTINCT w.id.levelType FROM WarpCtl w " +
            "WHERE w.id.insuranceLineCode = :lineCode " +
            "ORDER BY w.id.levelType",
            LevelType.class);
        query.setParameter("lineCode", insuranceLineCode);
        return query.getResultList();
    }
    
    /**
     * Save a new or update existing table assignment.
     */
    public WarpCtl save(WarpCtl warpCtl) {
        if (em.find(WarpCtl.class, warpCtl.getId()) == null) {
            em.persist(warpCtl);
            return warpCtl;
        } else {
            return em.merge(warpCtl);
        }
    }
    
    /**
     * Delete a table assignment.
     */
    public void delete(WarpCtl warpCtl) {
        em.remove(em.contains(warpCtl) ? warpCtl : em.merge(warpCtl));
    }
    
    /**
     * Delete all table assignments for an Insurance Line.
     */
    public int deleteByInsuranceLine(String insuranceLineCode) {
        return em.createQuery(
            "DELETE FROM WarpCtl w WHERE w.id.insuranceLineCode = :lineCode")
            .setParameter("lineCode", insuranceLineCode)
            .executeUpdate();
    }
    
    /**
     * Delete all assignments for a specific Insurance Line and Level Type.
     */
    public int deleteByInsuranceLineAndLevelType(String insuranceLineCode, LevelType levelType) {
        return em.createQuery(
            "DELETE FROM WarpCtl w WHERE w.id.insuranceLineCode = :lineCode " +
            "AND w.id.levelType = :levelType")
            .setParameter("lineCode", insuranceLineCode)
            .setParameter("levelType", levelType)
            .executeUpdate();
    }
}
