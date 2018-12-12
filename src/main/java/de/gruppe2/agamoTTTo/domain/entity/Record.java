package de.gruppe2.agamoTTTo.domain.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;

import static java.time.Duration.between;

@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "record")
public class Record {

    @Id
    @GeneratedValue
    @Column(name = "record_id")
    private Long id;

    @NotNull
    @Temporal(value = TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date")
    private Date date;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(name = "start_time")
    private LocalTime start_time;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(name = "end_time", nullable = false)
    private LocalTime end_time;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(name = "duration")
    private Time duration; //date

    @Size(min = 1, max = 250)
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "version")
    private Long version;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @CreatedBy
    private User userId;

    @ManyToOne
    @JoinColumn(name = "pool_id", nullable = false)
    private Pool poolId;

    public Record() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        version = 1L;
        /*
        try {
            duration = dateFormat.parse("12/12/2012");
        } catch (ParseException e) {
            System.out.println("bullshit");
        }
        //duration = start_time.compareTo(end_time); */
    }

    public Time calculateDuration(LocalTime start_time, LocalTime end_time) {
        Duration between = between(start_time, end_time);
        long millis = between.toMillis();
        Time time = new Time(0L);
        time.setTime(millis);
        return time;
    }

    public void setDuration(Time time, Record record) {
        record.duration = time;
    }


}
