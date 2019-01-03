package de.gruppe2.agamoTTTo.util;


import de.gruppe2.agamoTTTo.domain.entity.Record;
import de.gruppe2.agamoTTTo.domain.entity.RecordLog;
import de.gruppe2.agamoTTTo.domain.entity.Role;
import de.gruppe2.agamoTTTo.domain.entity.User;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * We use this component to process objects in Thymeleaf templates.
 * The scope of this is to display objects in a more beautiful way than the toString method
 * and thereby we can avoid too much boilerplate code in the Thymeleaf templates.
 * Furthermore by using this DisplayNameBuilder we can ensure the separation of model, view
 * and controller way better, since by using the toString method of an model object in a view
 * is a violation of this design pattern.
 */
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
                "(Dauer: " + this.convertMinutesToHoursAndMinutes(record.getDuration()) + "), " +
                "Beschreibung: " + record.getDescription() + ", " +
                "Pool: " + record.getPool().getName();
    }

    public String display(RecordLog recordLog){
        return recordLog.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " " +
                "von " + recordLog.getStartTime() + " bis " + recordLog.getEndTime() + " " +
                "(Dauer: " + this.convertMinutesToHoursAndMinutes(recordLog.getDuration()) + "), " +
                "Beschreibung: " + recordLog.getDescription() + ", " +
                "Pool: " + recordLog.getPool().getName();
    }

    public String display(Role role) {
        String roleName = role.getRoleName().substring(5).toLowerCase();

        return roleName.substring(0, 1).toUpperCase() + roleName.substring(1);
    }

    public String convertMinutesToHoursAndMinutes(Long minutes){
        return LocalTime.MIN.plus(Duration.ofMinutes(minutes)).toString() + " h";
    }
}