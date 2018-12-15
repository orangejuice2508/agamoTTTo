package de.gruppe2.agamoTTTo.repository;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import de.gruppe2.agamoTTTo.domain.entity.RecordLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordLogRepository extends JpaRepository<RecordLog, Long> {
    List<RecordLog> findAllByPoolAndDateBetweenOrderByDateDesc(Pool pool, LocalDate from, LocalDate to);
}
