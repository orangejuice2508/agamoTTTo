package de.gruppe2.agamoTTTo.repository;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import de.gruppe2.agamoTTTo.domain.entity.RecordLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This repository is used for crud operations (create/read/update/delete) on the table "record_log" in the database.
 * Note: The queries are constructed automatically by Spring JPA based on method naming conventions.
 */
@Repository
public interface RecordLogRepository extends JpaRepository<RecordLog, Long> {
    List<RecordLog> findAllByPoolAndChangeAtBetweenOrderByChangeAtDesc(Pool pool, LocalDateTime from, LocalDateTime to);
}
