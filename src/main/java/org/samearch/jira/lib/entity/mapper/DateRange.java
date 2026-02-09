package org.samearch.jira.lib.entity.mapper;

import java.time.ZonedDateTime;
import java.util.Objects;

public class DateRange {

    private final ZonedDateTime startDate;
    private final ZonedDateTime endDate;

    public DateRange(ZonedDateTime startDate, ZonedDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ZonedDateTime startDate() {
        return startDate;
    }

    public ZonedDateTime endDate() {
        return endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateRange)) return false;

        DateRange dateRange = (DateRange) o;

        if (!Objects.equals(startDate, dateRange.startDate)) return false;
        return Objects.equals(endDate, dateRange.endDate);
    }

    @Override
    public int hashCode() {
        int result = startDate != null ? startDate.hashCode() : 0;
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }

}
