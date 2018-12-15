package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.domain.base.PoolDateFilter;
import de.gruppe2.agamoTTTo.domain.entity.RecordLog;
import de.gruppe2.agamoTTTo.repository.RecordLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecordLogService {

    private RecordLogRepository recordLogRepository;

    @Autowired
    public RecordLogService(RecordLogRepository recordLogRepository) {
        this.recordLogRepository = recordLogRepository;
    }

    /**
     * This method returns all RecordLogs which match the criteria of the filter.
     *
     * @param filter contains the criteria set by the user
     * @return an ordered list with RecordLogs which match the criteria of the filter
     */
    public List<RecordLog> getAllRecordLogsByFilter(PoolDateFilter filter){
        // If no date is set, set it to a default date. Reason: Date is optional in the filter.
        LocalDate from = filter.getFrom() != null ? filter.getFrom() : LocalDate.of(1000,1,1);
        LocalDate to = filter.getTo() != null ? filter.getTo() : LocalDate.of(9999,12,31);

        return recordLogRepository.findAllByPoolAndDateBetweenOrderByDateDesc(filter.getPool(), from, to);
    }
}
