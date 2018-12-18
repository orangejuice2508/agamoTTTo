package de.gruppe2.agamoTTTo.domain.base.filter;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Used for filtering entities which contain pools and dates (e.g. Records, RecordLogs)
 */
@Getter
@Setter
@NoArgsConstructor
public class PoolDateFilter extends DateFilter {

    private Pool pool;

    public PoolDateFilter(LocalDate today) {
        super(today);
    }

    public PoolDateFilter(PoolDateFilter filter) {
        super(filter);
        this.pool = filter.getPool();
    }
}