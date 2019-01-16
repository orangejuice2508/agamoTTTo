package de.gruppe2.agamoTTTo.util;


import de.gruppe2.agamoTTTo.domain.entity.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * We use this component to process objects in Thymeleaf templates.
 * The scope of this is to display objects in a more beautiful way than the toString method can offer.
 * Thereby we can avoid too much boilerplate code in the Thymeleaf templates.
 * Furthermore by using this DisplayNameBuilder we can ensure the separation of model, view
 * and controller, since using the toString method of an entity object in a view is a violation of this design pattern.
 */
@Component
public final class DisplayNameBuilder {

    // Display a user.
    public String display(User user) {
        return user.getLastName() + ", " +
                user.getFirstName() + " (" +
                user.getEmail() + ")";
    }

    // Display a record
    public String display(Record record){
        return record.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " " +
                "von " + record.getStartTime() + " bis " + record.getEndTime() + " " +
                "(Dauer: " + this.convertMinutesToHoursAndMinutes(record.getDuration()) + "), " +
                "Beschreibung: " + record.getDescription() + ", " +
                "Pool: " + record.getPool().getName();
    }

    // Display a recordLog
    public String display(RecordLog recordLog){
        return recordLog.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " " +
                "von " + recordLog.getStartTime() + " bis " + recordLog.getEndTime() + " " +
                "(Dauer: " + this.convertMinutesToHoursAndMinutes(recordLog.getDuration()) + "), " +
                "Beschreibung: " + recordLog.getDescription() + ", " +
                "Pool: " + recordLog.getPool().getName();
    }

    // Display a role.
    public String display(Role role) {
        String roleName = role.getRoleName().substring(5).toLowerCase();

        return roleName.substring(0, 1).toUpperCase() + roleName.substring(1);
    }

    // Display a userPool assignment.
    public String display(UserPool userPool, Boolean displayPool) {
        String userPoolWithStatus;

        if (displayPool) {
            userPoolWithStatus = userPool.getPool().getName();
        } else {
            userPoolWithStatus = display(userPool.getUser());
        }

        if (!userPool.getIsActive()) {
            userPoolWithStatus = userPoolWithStatus.concat(" [INAKTIV]");
        }

        return userPoolWithStatus;
    }

    // Convert minutes (e.g. 300) to hours and minutes (300min = 5:00h).
    public String convertMinutesToHoursAndMinutes(Long minutes){
        return LocalTime.MIN.plus(Duration.ofMinutes(minutes)).toString() + " h";
    }
}