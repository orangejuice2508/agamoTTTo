package de.gruppe2.agamoTTTo.domain.bo.filter;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.Period;

/**
 * Used for filtering entities which contain pools and dates (e.g. Records, RecordLogs)
 */
@Getter
@Setter
@NoArgsConstructor
public class PoolDateFilter {

    private Pool pool;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate from;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate to;

    public PoolDateFilter(LocalDate today) {
        LocalDate oneMonthAgo = today.minus(Period.ofMonths(1));
        this.from = oneMonthAgo.withDayOfMonth(1);
        this.to = oneMonthAgo.withDayOfMonth(oneMonthAgo.lengthOfMonth());
    }

    /**
     * This method is for constructing a new filter based on a filter handed over as parameter.
     * So for example default dates are created or wrong dates are exchanged.
     *
     * @param filter the filter on which the new filter should be based
     */
    public PoolDateFilter(PoolDateFilter filter) {
        // If one or both dates is NOT set, set it to default values. Reason: Date is optional in the filter.
        this.from = filter.getFrom() != null ? filter.getFrom() : LocalDate.of(1000, 1, 1);
        this.to = filter.getTo() != null ? filter.getTo() : LocalDate.of(9999, 12, 31);

        // If the toDate is before the fromDate, then exchange both values.
        if (this.to.isBefore(this.from)) {
            LocalDate fromDate = this.from;
            this.from = this.to;
            this.to = fromDate;
        }

        // The pool remains the same
        this.pool = filter.getPool();
    }
}
