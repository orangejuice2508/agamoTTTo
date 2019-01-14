package de.gruppe2.agamoTTTo.repository;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This repository is used for crud operations (create/read/update/delete) on the table "pool" in the database.
 * Note: The queries are constructed automatically by Spring JPA based on method naming conventions.
 */
@Repository
public interface PoolRepository extends JpaRepository<Pool, Long> {
    List<Pool> findAllByOrderByNameAsc();
}
