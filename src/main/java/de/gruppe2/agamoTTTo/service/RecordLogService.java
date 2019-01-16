package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.domain.bo.filter.PoolDateFilter;
import de.gruppe2.agamoTTTo.domain.entity.RecordLog;
import de.gruppe2.agamoTTTo.repository.RecordLogRepository;
import de.gruppe2.agamoTTTo.security.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service which is used for dealing with the recordLogs("Logdateien") of our application.
 */
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
    @PreAuthorize(Permission.VORGESETZTER)
    public List<RecordLog> getAllRecordLogsByFilter(PoolDateFilter filter){
        // Update filter so that empty dates are filled with default values
        filter = new PoolDateFilter(filter);

        return recordLogRepository.
                findAllByPoolAndChangeAtBetweenOrderByChangeAtDesc(
                        filter.getPool(),
                        filter.getFrom().atTime(0,0,0,0),
                        filter.getTo().atTime(23,59,59,59));
    }
}
