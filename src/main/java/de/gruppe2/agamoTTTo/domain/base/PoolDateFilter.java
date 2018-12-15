package de.gruppe2.agamoTTTo.domain.base;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Used for filtering entities which contain pools and dates (e.g. Records, RecordLogs)
 */
@Getter
@Setter
public class PoolDateFilter {

    private Pool pool;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate from;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate to;

}
