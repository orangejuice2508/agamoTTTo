package de.gruppe2.agamoTTTo.domain.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.Temporal;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
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
    private Date date;

    @Column(name = "start_time")
    private Time start_time;

    @Column(name = "end_time")
    private Time end_time;

    @Column(name = "duration")
    private Time duration;

    @Size(max = 250)
    @Column(name = "description")
    private String descrition;

    @Column(name = "version")
    private Long version = 1L;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @CreatedBy
    private User user_id;

    @ManyToOne
    @JoinColumn(name = "pool_id")
    private Pool pool_id;

    public Record() {}



}
