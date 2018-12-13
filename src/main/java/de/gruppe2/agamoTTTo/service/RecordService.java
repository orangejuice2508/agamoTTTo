package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.domain.entity.Record;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static java.time.Duration.between;

@Service
public class RecordService{


    private RecordRepository recordRepository;

    @Autowired
    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;

    }

    /**
     * This method uses the recordRepository to try to add a record to the database.
     *
     * @param record the record as obtained from the controller
     */
    public void addRecord(Record record) {

        record.setDuration(calculateDuration(record.getStartTime(), record.getEndTime()));
        recordRepository.save(record);
    }

    /**
     * This method uses the recordRepository to find all belonging Records of a User.
     *
     * @param user The current User as obtained from the controller
     * @return All records of the User
     */
    private List<Record> getAllRecordsOfAUser(User user) {
        return recordRepository.findAllByUser(user);
    }

    /**
     * This method calculates the duration of time a user worked.
     *
     * @param startTime The start time of the task
     * @param endTime the end time of the task
     * @return The duration
     */
    private Time calculateDuration(LocalTime startTime, LocalTime endTime) {

        Duration duration = between(startTime, endTime);
        long millis = duration.toMillis();
        return new Time(millis);
    }

    /**
     * This method checks if a record's times are valid.
     *
     * @param record the new record
     * @return true if endTime is after startTime
     */
    public boolean areTimesValid(Record record) {
        return record.getEndTime().isAfter(record.getStartTime());
    }

    public boolean areTimesAllowed(Record newRecord, User user) {

        LocalTime newStartTime = newRecord.getStartTime();
        LocalTime newEndTime = newRecord.getEndTime();
        LocalDate newDate = newRecord.getDate();
        List<Record> currentUserRecords = getAllRecordsOfAUser(user);
        if (!currentUserRecords.isEmpty()) {

            for (Record record : currentUserRecords) {
                LocalTime testingRecordStartTime = record.getStartTime();
                LocalTime testingRecordEndTime = record.getEndTime();
                LocalDate testingDate = record.getDate();

                if (newDate.equals(testingDate)) {
                    if (newStartTime.isBefore(testingRecordEndTime) && newStartTime.isAfter(testingRecordStartTime)) {
                        return false;
                    }

                    if (newEndTime.isAfter(testingRecordStartTime) && newEndTime.isBefore(testingRecordEndTime)) {
                        return false;
                    }

                    if (newStartTime.isAfter(testingRecordStartTime) && newEndTime.isBefore(testingRecordEndTime)) {
                        return false;
                    }

                    if (newStartTime.isBefore(testingRecordStartTime) && newEndTime.isAfter(testingRecordEndTime)) {
                        return false;
                    }

                    if (newStartTime.equals(testingRecordStartTime)) {
                        return false;
                    }

                    if (newEndTime.equals(testingRecordEndTime)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return true;
    }
}
