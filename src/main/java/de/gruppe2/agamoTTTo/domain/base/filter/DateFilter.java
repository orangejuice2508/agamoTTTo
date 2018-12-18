package de.gruppe2.agamoTTTo.domain.base.filter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.Period;

/**
 * Used for filtering entities which contain dates (e.g. Records)
 */
@Getter
@Setter
@NoArgsConstructor
public class DateFilter {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate from;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate to;

    public DateFilter(LocalDate today) {
        LocalDate oneMonthAgo = today.minus(Period.ofMonths(1));
        this.from = oneMonthAgo.withDayOfMonth(1);
        this.to = oneMonthAgo.withDayOfMonth(oneMonthAgo.lengthOfMonth());
    }

    public DateFilter(DateFilter filter) {
        // If no date is set, set it to a default date. Reason: Date is optional in the filter.
        this.from = filter.getFrom() != null ? filter.getFrom() : LocalDate.of(1000,1,1);
        this.to = filter.getTo() != null ? filter.getTo() : LocalDate.of(9999,12,31);
    }
}
