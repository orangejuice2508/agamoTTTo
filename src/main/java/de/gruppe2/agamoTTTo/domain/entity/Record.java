package de.gruppe2.agamoTTTo.domain.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Set;
import java.util.Date;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "record")
public class Record {

    @Id
    @GeneratedValue
    @Column(name = "record_id")
    private Long id;

    @Column(name = "date")
    private Date date;

    @Column(name = "start_time")
    private Timestamp startTime;

    @Column(name = "end_time")
    private Timestamp endTime;

    @Column(name = "duration")
    private Timestamp duration = calculateTimeDifference(startTime, endTime);

    @Size(max = 250)
    @Column(name = "description")
    private String descripiton;

    @Column(name = "version")
    private int version;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @CreatedBy
    private User user_id;

   //TO-DO @Column(name = "pool_id")

    @ManyToMany(mappedBy = "records")
    private Set<User> users;




    private Timestamp calculateTimeDifference(Timestamp startTime, Timestamp endTime) {

        long diff = endTime.getTime() - startTime.getTime();
        Timestamp difference = new Timestamp(diff);
        return null;
    }
}
