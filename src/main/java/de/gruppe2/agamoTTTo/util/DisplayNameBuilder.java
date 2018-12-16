package de.gruppe2.agamoTTTo.util;


import de.gruppe2.agamoTTTo.domain.entity.Record;
import de.gruppe2.agamoTTTo.domain.entity.RecordLog;
import de.gruppe2.agamoTTTo.domain.entity.User;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public final class DisplayNameBuilder {
    public String display(User user) {
        return user.getFirstName() + " " +
                user.getLastName() + " (" +
                user.getEmail() + ")";
    }

    public String display(Record record){
        return record.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " " +
                 "von " + record.getStartTime() + " bis " + record.getEndTime() + " " +
                "(Dauer: " + record.getDuration() + "), " +
                "Beschreibung: " + record.getDescription() + ", " +
                "Pool: " + record.getPool().getName();
    }

    public String display(RecordLog recordLog){
        return recordLog.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " " +
                "von " + recordLog.getStartTime() + " bis " + recordLog.getEndTime() + " " +
                "(Dauer: " + recordLog.getDuration() + "), " +
                "Beschreibung: " + recordLog.getDescription() + ", " +
                "Pool: " + recordLog.getPool().getName();
    }
}