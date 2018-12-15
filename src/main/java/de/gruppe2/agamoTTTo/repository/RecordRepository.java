package de.gruppe2.agamoTTTo.repository;

import de.gruppe2.agamoTTTo.domain.entity.Record;
import de.gruppe2.agamoTTTo.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long>{
    Set<Record> findAllByUserAndDate(User user, LocalDate date);
}
