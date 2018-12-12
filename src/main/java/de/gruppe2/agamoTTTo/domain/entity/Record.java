package de.gruppe2.agamoTTTo.domain.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.Temporal;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.Calendar;
import java.util.Set;

import static ognl.OgnlOps.longValue;

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

    @CreatedDate
    @Column(name = "date")
    private Calendar date;

    @Column(name = "start_time", nullable = false)
    private Calendar start_time;

    @Column(name = "end_time", nullable = false)
    private Calendar end_time;

    @Column(name = "duration")
    private Calendar duration;

    @Size(max = 250)
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "version")
    private Long version;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @CreatedBy
    private User user_id;

    @ManyToOne
    @JoinColumn(name = "pool_id", nullable = false)
    private Pool pool_id;

    public Record() {
        version = 1L;
        //duration = start_time.compareTo(end_time);
    }



}
