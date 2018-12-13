package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.domain.entity.Record;
import de.gruppe2.agamoTTTo.security.CustomSecurityUser;
import de.gruppe2.agamoTTTo.repository.PoolRepository;
import de.gruppe2.agamoTTTo.repository.RecordRepository;
import de.gruppe2.agamoTTTo.repository.UserRepository;
import de.gruppe2.agamoTTTo.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

import static java.time.Duration.between;

@Service
public class RecordService{

    private PoolRepository poolRepository;
    private UserRepository userRepository;
    private RecordRepository recordRepository;

    @Autowired
    public RecordService(PoolRepository poolRepository, UserRepository userRepository, RecordRepository recordRepository) {
        this.poolRepository = poolRepository;
        this.userRepository = userRepository;
        this.recordRepository = recordRepository;

    }

    /**
     * This method uses the recordRepository to try to add a record to the database.
     *
     * @param record the record as obtained from the controller
     */
    public void addRecord(Record record) {
        record.setDuration(calculateDuration(record.getStart_time(), record.getEnd_time()));
        //viele if-abfragen
        recordRepository.save(record);
    }

    /**
     * This method uses the recordRepository to find all belonging Records of a User.
     *
     * @param user The current User as obtained from the controller
     * @return All records of the User
     */
    public List<Record> getAllRecordsOfAUser(User user) {
        System.out.println(recordRepository.findAllByUserId(user).toString());
        return recordRepository.findAllByUserId(user);
    }

    /**
     * This method calculates the duration of time a user worked.
     *
     * @param start_time The start time of the task
     * @param end_time the end time of the task
     * @return The duration
     */
    public Time calculateDuration(LocalTime start_time, LocalTime end_time) {
        Duration between = between(start_time, end_time);
        long millis = between.toMillis();
        Time time = new Time(0L);
        time.setTime(millis);
        return time;
    }
}
