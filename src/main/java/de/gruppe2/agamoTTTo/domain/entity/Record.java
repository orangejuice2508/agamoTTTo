package de.gruppe2.agamoTTTo.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

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

    @Column(name = "duration_in_minutes")
    private Long duration;

    @NotEmpty
    @Size(max = 250)
    @Column(name = "description")
    private String description;

    @Column(name = "version")
    @Version
    private Long version;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @CreatedBy
    private User user;

    @ManyToOne
    @JoinColumn(name = "pool_id", nullable = false)
    private Pool pool;

    @NotNull
    @Column(name = "is_deleted")
    private Boolean isDeleted = Boolean.FALSE;

    public boolean equals(Record anotherRecord) {
        return id.equals(anotherRecord.getId()) && date.equals(anotherRecord.getDate())
                && startTime.equals(anotherRecord.getStartTime()) && description.equals(anotherRecord.getDescription())
                && pool.equals(anotherRecord.getPool()) && isDeleted.equals(anotherRecord.getIsDeleted());
    }
}
