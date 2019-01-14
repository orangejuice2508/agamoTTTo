package de.gruppe2.agamoTTTo.domain.entity;

import de.gruppe2.agamoTTTo.domain.base.ChangeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * This class corresponds to the database table "record_log".
 * Its columns correspond to the attributes of this class.
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "record_log")
public class RecordLog {
    @Id
    @GeneratedValue
    @Column(name = "record_log_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "record_id")
    private Record record;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date")
    private LocalDate date;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(name = "start_time")
    private LocalTime startTime;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(name = "end_time")
    private LocalTime endTime;

    @NotNull
    @Column(name = "duration_in_minutes")
    private Long duration;

    @NotEmpty
    @Size(max = 250)
    @Column(name = "description")
    private String description;

    @Column(name = "version")
    private Long version;

    @ManyToOne
    @JoinColumn(name = "change_by", nullable = false)
    @LastModifiedBy
    private User changeBy;

    @Column(name = "change_at")
    @LastModifiedDate
    private LocalDateTime changeAt;

    @Column(name = "change_type")
    @Enumerated(EnumType.STRING)
    private ChangeType changeType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "pool_id", nullable = false)
    private Pool pool;

    /*
        The RecordLog is instantiated with data from the original
        record object, before it was modified/deleted.
     */
    public RecordLog(Record originalRecord, ChangeType changeType) {
        this.record = originalRecord;
        this.date = originalRecord.getDate();
        this.startTime = originalRecord.getStartTime();
        this.endTime = originalRecord.getEndTime();
        this.duration = originalRecord.getDuration();
        this.description = originalRecord.getDescription();
        this.version = originalRecord.getVersion();
        this.changeType = changeType;
        this.user = originalRecord.getUser();
        this.pool = originalRecord.getPool();
    }
}
