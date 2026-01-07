package genowa.repository;

import genowa.model.GenTable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Repository for GenTable (gen_tables) operations.
 */
public class GenTableRepository
{
    private final EntityManager em;

    public GenTableRepository(EntityManager em)
    {
        this.em = em;
    }

    /**
     * Find all tables ordered by name.
     */
    public List<GenTable> findAll()
    {
        TypedQuery<GenTable> query = em.createQuery(
            "SELECT t FROM GenTable t ORDER BY t.tableNm", GenTable.class);
        return query.getResultList();
    }

    /**
     * Find table by index number (primary key).
     */
    public Optional<GenTable> findByIndex(Integer tableIndexNbr)
    {
        GenTable table = em.find(GenTable.class, tableIndexNbr);
        return Optional.ofNullable(table);
    }

    /**
     * Find table by name.
     */
    public Optional<GenTable> findByName(String tableNm)
    {
        TypedQuery<GenTable> query = em.createQuery(
            "SELECT t FROM GenTable t WHERE t.tableNm = :name", GenTable.class);
        query.setParameter("name", tableNm);
        List<GenTable> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Find all distinct table names.
     */
    public List<String> findAllTableNames()
    {
        TypedQuery<String> query = em.createQuery(
            "SELECT t.tableNm FROM GenTable t ORDER BY t.tableNm", String.class);
        return query.getResultList();
    }

    /**
     * Search tables by name pattern.
     */
    public List<GenTable> searchByName(String pattern)
    {
        TypedQuery<GenTable> query = em.createQuery(
            "SELECT t FROM GenTable t WHERE UPPER(t.tableNm) LIKE UPPER(:pattern) ORDER BY t.tableNm", 
            GenTable.class);
        query.setParameter("pattern", "%" + pattern + "%");
        return query.getResultList();
    }

    /**
     * Get count of all tables.
     */
    public long count()
    {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(t) FROM GenTable t", Long.class);
        return query.getSingleResult();
    }

    /**
     * Persist a new table.
     */
    public void persist(GenTable table)
    {
        em.persist(table);
    }

    /**
     * Merge (update) an existing table.
     */
    public GenTable merge(GenTable table)
    {
        return em.merge(table);
    }

    /**
     * Delete a table.
     */
    public void delete(GenTable table)
    {
        em.remove(em.contains(table) ? table : em.merge(table));
    }
}
