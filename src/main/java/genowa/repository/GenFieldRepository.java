package genowa.repository;

import genowa.model.GenField;
import genowa.model.GenFieldId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Repository for GenField (gen_fields) operations.
 */
public class GenFieldRepository
{
    private final EntityManager em;

    public GenFieldRepository(EntityManager em)
    {
        this.em = em;
    }

    /**
     * Find all fields for a given table (by table index).
     */
    public List<GenField> findByTableIndex(Integer tableIndexNbr)
    {
        TypedQuery<GenField> query = em.createQuery(
            "SELECT f FROM GenField f WHERE f.tableIndexNbr = :tableIndex ORDER BY f.keyCctNbr",
            GenField.class);
        query.setParameter("tableIndex", tableIndexNbr);
        return query.getResultList();
    }

    /**
     * Find all fields for a given table name.
     */
    public List<GenField> findByTableName(String tableNm)
    {
        TypedQuery<GenField> query = em.createQuery(
            "SELECT f FROM GenField f WHERE f.genTable.tableNm = :tableName ORDER BY f.keyCctNbr",
            GenField.class);
        query.setParameter("tableName", tableNm);
        return query.getResultList();
    }

    /**
     * Find field by composite primary key.
     */
    public Optional<GenField> findById(Integer tableIndexNbr, Integer keyCctNbr)
    {
        GenField field = em.find(GenField.class, new GenFieldId(tableIndexNbr, keyCctNbr));
        return Optional.ofNullable(field);
    }

    /**
     * Find key fields for a table.
     */
    public List<GenField> findKeyFields(Integer tableIndexNbr)
    {
        TypedQuery<GenField> query = em.createQuery(
            "SELECT f FROM GenField f WHERE f.tableIndexNbr = :tableIndex AND f.keyFlagCd = 'Y' ORDER BY f.keyCctNbr",
            GenField.class);
        query.setParameter("tableIndex", tableIndexNbr);
        return query.getResultList();
    }

    /**
     * Search fields by column name pattern across all tables.
     */
    public List<GenField> searchByColumnName(String pattern)
    {
        TypedQuery<GenField> query = em.createQuery(
            "SELECT f FROM GenField f WHERE UPPER(f.columnNm) LIKE UPPER(:pattern) ORDER BY f.columnNm",
            GenField.class);
        query.setParameter("pattern", "%" + pattern + "%");
        return query.getResultList();
    }

    /**
     * Search fields by description/alias pattern.
     */
    public List<GenField> searchByDescription(String pattern)
    {
        TypedQuery<GenField> query = em.createQuery(
            "SELECT f FROM GenField f WHERE UPPER(f.longAliasNm) LIKE UPPER(:pattern) ORDER BY f.columnNm",
            GenField.class);
        query.setParameter("pattern", "%" + pattern + "%");
        return query.getResultList();
    }

    /**
     * Get distinct column descriptions for autocomplete.
     */
    public List<String> findDistinctDescriptions()
    {
        TypedQuery<String> query = em.createQuery(
            "SELECT DISTINCT f.longAliasNm FROM GenField f WHERE f.longAliasNm IS NOT NULL AND f.longAliasNm <> '' ORDER BY f.longAliasNm",
            String.class);
        query.setMaxResults(100);
        return query.getResultList();
    }

    /**
     * Get total count of fields.
     */
    public long count()
    {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(f) FROM GenField f", Long.class);
        return query.getSingleResult();
    }

    /**
     * Persist a new field.
     */
    public void persist(GenField field)
    {
        em.persist(field);
    }

    /**
     * Merge (update) an existing field.
     */
    public GenField merge(GenField field)
    {
        return em.merge(field);
    }

    /**
     * Delete a field.
     */
    public void delete(GenField field)
    {
        em.remove(em.contains(field) ? field : em.merge(field));
    }
}
