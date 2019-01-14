package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.domain.base.filter.PoolDateFilter;
import de.gruppe2.agamoTTTo.domain.entity.Record;
import de.gruppe2.agamoTTTo.domain.entity.RecordLog;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.repository.RecordLogRepository;
import de.gruppe2.agamoTTTo.repository.RecordRepository;
import de.gruppe2.agamoTTTo.domain.base.ChangeType;
import de.gruppe2.agamoTTTo.security.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.time.Duration.between;

/**
 * Service which is used for dealing with the records("Einträge") of our application.
 */
@Service
public class RecordService{

    private RecordRepository recordRepository;

    private RecordLogRepository recordLogRepository;

    @Autowired
    public RecordService(RecordRepository recordRepository, RecordLogRepository recordLogRepository) {
        this.recordRepository = recordRepository;
        this.recordLogRepository = recordLogRepository;
    }

    /**
     * This method uses the recordRepository to try to add a record to the database.
     * Furthermore the new record is logged in the database.
     *
     * @param record the record as obtained from the controller
     */
    @PreAuthorize(Permission.MITARBEITER)
    public void addRecord(Record record) {
        // Calculate and set the duration of the record.
        record.setDuration(calculateDuration(record.getStartTime(), record.getEndTime()));

        // Save the record to the database.
        record = recordRepository.save(record);

        // Log the record after saving it.
        recordLogRepository.save(new RecordLog(record, ChangeType.created));
    }

    /**
     * This method uses the recordRepository to find a certain record from the database.
     * If no record was found, an empty optional object is returned.
     *
     * @param id the id of the record, which should be found
     * @return the optional record
     */
    @PreAuthorize(Permission.MITARBEITER)
    public Optional<Record> findRecordById(Long id) {
        return recordRepository.findById(id);
    }

    /**
     * This method returns all records of a user which match the criteria of the filter.
     *
     * @param filter contains the criteria set by the user
     * @param user the user whose records should be found
     * @return an ordered list with Records which match the criteria of the filter
     */
    @PreAuthorize(Permission.MITARBEITER)
    public List<Record> getAllRecordsByFilter(PoolDateFilter filter, User user) {
        // Update filter so that empty dates are filled with default values
        filter = new PoolDateFilter(filter);

        return recordRepository.findAllByUserAndPoolAndDateBetweenAndIsDeletedIsFalseOrderByDateAscStartTimeAsc(user, filter.getPool(), filter.getFrom(), filter.getTo());
    }

    /**
     * This method uses the recordRepository to try to update the record in the database.
     *  Furthermore the unchanged record is logged in the database.
     *
     * @param updatedRecord the updated record as obtained from the controller
     */
    @PreAuthorize(Permission.MITARBEITER)
    public void updateRecord(Record updatedRecord) {

        // Use the getOne method, so that no more DB fetch has to be executed
        Record recordToUpdate = recordRepository.getOne(updatedRecord.getId());

        // Check whether the record was really edited.
        if (!recordToUpdate.equals(updatedRecord)) {
            // Log the record before changing it.
            recordLogRepository.save(new RecordLog(recordToUpdate, ChangeType.modified));

            // Manipulate the recordToUpdate with the updated fields
            recordToUpdate.setDate(updatedRecord.getDate());
            recordToUpdate.setStartTime(updatedRecord.getStartTime());
            recordToUpdate.setEndTime(updatedRecord.getEndTime());
            recordToUpdate.setDescription(updatedRecord.getDescription());
            recordToUpdate.setPool(updatedRecord.getPool());
            recordToUpdate.setDuration(calculateDuration(updatedRecord.getStartTime(), updatedRecord.getEndTime()));
            recordToUpdate.setIsDeleted(updatedRecord.getIsDeleted());

            // Save the record to the database.
            recordRepository.save(recordToUpdate);
        }
    }

    /**
     * This method uses the recordRepository to try to "delete" the record from the database.
     * Furthermore the "deleted" record is logged in the database.
     * Note: The record is not deleted, but flagged as "deleted" in the database.
     *
     * @param record the record that should be "deleted"
     */
    @PreAuthorize(Permission.MITARBEITER)
    public void deleteRecord(Record record) {

        // Use the getOne method, so that no more DB fetch has to be executed.
        Record recordToDelete = recordRepository.getOne(record.getId());

        // Log the current record before "deleting" it.
        recordLogRepository.save(new RecordLog(recordToDelete, ChangeType.deleted));

        // "Delete" the record by setting the isDeleted field to true.
        recordToDelete.setIsDeleted(Boolean.TRUE);

        // Save the record to the database.
        recordRepository.save(recordToDelete);
    }

    /**
     * This method checks if a record's times are valid.
     *
     * @param record the new record
     * @return true if endTime is after startTime
     */
    @PreAuthorize(Permission.MITARBEITER)
    public boolean areTimesValid(Record record) {
        return record.getEndTime().isAfter(record.getStartTime());
    }

    /**
     * This method checks if a record's time are allowed, i.e. that the times of the new record
     * do NOT overlap with an already existing record.
     *
     * @param newRecord the record which should be saved
     * @param user the user who wants to add a record
     * @return true if the times are allowed (i.e. do NOT overlap); false if the time are not allowed
     */
    @PreAuthorize(Permission.MITARBEITER)
    public boolean areTimesAllowed(Record newRecord, User user) {

        LocalTime newStartTime = newRecord.getStartTime();
        LocalTime newEndTime = newRecord.getEndTime();
        LocalDate newDate = newRecord.getDate();
        Long newId = newRecord.getId();

        // Get a user's current records of the specific day when he wants to add his new record
        Set<Record> currentUserRecords = recordRepository.findAllByUserAndDateAndIsDeletedIsFalse(user, newDate);

        if (!currentUserRecords.isEmpty()) {
            for (Record currentRecord : currentUserRecords) {
                LocalTime currentStartTime = currentRecord.getStartTime();
                LocalTime currentEndTime = currentRecord.getEndTime();

                /*
                    This if clause is very important:
                        1. If a user ADDs a new record, then the newId is null.
                        So it has to be checked against each and every other existing record.
                        2. If a user EDITs an existing record, this edited record has to be checked
                        only against the other records AND not against THIS record.
                 */
                if (newId == null || !newId.equals(currentRecord.getId())) {


                    // Check if the new record starts between the times of a current record.
                    if (newStartTime.isBefore(currentEndTime) && newStartTime.isAfter(currentStartTime)) {
                        return false;
                    }

                    // Check if the new record ends between the times of a current record.
                    if (newEndTime.isAfter(currentStartTime) && newEndTime.isBefore(currentEndTime)) {
                        return false;
                    }

                    // Check if the new record lies completely between the times of a current record.
                    if (newStartTime.isAfter(currentStartTime) && newEndTime.isBefore(currentEndTime)) {
                        return false;
                    }

                    // Check if the new record encompasses the times of a current record.
                    if (newStartTime.isBefore(currentStartTime) && newEndTime.isAfter(currentEndTime)) {
                        return false;
                    }

                    // Check if the new record starts or ends at the start or end of a current record.
                    if (newStartTime.equals(currentStartTime) || newEndTime.equals(currentEndTime)) {
                        return false;
                    }
                }
            }
        }

        // If the user doesn't have any records on that day, the times are allowed.
        return true;
    }

    /**
     * This method calculates the duration of time (in minutes).
     *
     * @param startTime the start time of the record
     * @param endTime the end time of the record
     * @return the duration as the time between endTime and startTime in minutes
     */
    @PreAuthorize(Permission.MITARBEITER)
    private Long calculateDuration(LocalTime startTime, LocalTime endTime) {
        return between(startTime, endTime).toMinutes();
    }

    /**
     * This method calculates the total duration of a lists of records.
     *
     * @param records the list with the records of which the total duration should be calculated
     * @return the total duration in minutes
     */
    @PreAuthorize(Permission.MITARBEITER)
    public Long calculateTotalDuration(List<Record> records) {
        return records.stream().mapToLong(Record::getDuration).sum();
    }
}
