package genowa.repository;

import genowa.model.GenAlgorithm;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for GenAlgorithm entities.
 * Provides database access for algorithm persistence.
 */
public class GenAlgorithmRepository {
    
    private final EntityManager em;
    
    public GenAlgorithmRepository(EntityManager em) {
        this.em = em;
    }
    
    /**
     * Save or update an algorithm.
     */
    public GenAlgorithm save(GenAlgorithm algorithm) {
        if (algorithm.getAlgorithmId() == null) {
            em.persist(algorithm);
        } else {
            algorithm = em.merge(algorithm);
        }
        return algorithm;
    }
    
    /**
     * Find algorithm by ID.
     */
    public Optional<GenAlgorithm> findById(Integer algorithmId) {
        GenAlgorithm algorithm = em.find(GenAlgorithm.class, algorithmId);
        return Optional.ofNullable(algorithm);
    }
    
    /**
     * Find algorithm by name.
     */
    public Optional<GenAlgorithm> findByName(String algorithmName) {
        TypedQuery<GenAlgorithm> query = em.createQuery(
            "SELECT a FROM GenAlgorithm a WHERE a.algorithmName = :name",
            GenAlgorithm.class);
        query.setParameter("name", algorithmName);
        List<GenAlgorithm> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    /**
     * Find all algorithms.
     */
    public List<GenAlgorithm> findAll() {
        TypedQuery<GenAlgorithm> query = em.createQuery(
            "SELECT a FROM GenAlgorithm a ORDER BY a.algorithmName, a.effectiveDate",
            GenAlgorithm.class);
        return query.getResultList();
    }
    
    /**
     * Find algorithms by insurance line.
     */
    public List<GenAlgorithm> findByInsuranceLine(String insuranceLineCode) {
        TypedQuery<GenAlgorithm> query = em.createQuery(
            "SELECT a FROM GenAlgorithm a WHERE a.insuranceLineCode = :line " +
            "ORDER BY a.algorithmName, a.effectiveDate",
            GenAlgorithm.class);
        query.setParameter("line", insuranceLineCode);
        return query.getResultList();
    }
    
    /**
     * Find active algorithms (effective date <= today <= expiration date or null).
     */
    public List<GenAlgorithm> findActive(LocalDate asOfDate) {
        TypedQuery<GenAlgorithm> query = em.createQuery(
            "SELECT a FROM GenAlgorithm a WHERE " +
            "a.effectiveDate <= :asOfDate AND " +
            "(a.expirationDate IS NULL OR a.expirationDate >= :asOfDate) " +
            "ORDER BY a.algorithmName",
            GenAlgorithm.class);
        query.setParameter("asOfDate", asOfDate);
        return query.getResultList();
    }
    
    /**
     * Delete an algorithm.
     */
    public void delete(GenAlgorithm algorithm) {
        em.remove(algorithm);
    }
    
    /**
     * Delete algorithm by ID.
     */
    public void deleteById(Integer algorithmId) {
        GenAlgorithm algorithm = em.find(GenAlgorithm.class, algorithmId);
        if (algorithm != null) {
            em.remove(algorithm);
        }
    }
}

