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

import java.util.*;

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
           //viele if-abfragen
           recordRepository.save(record);
       }



}
