package org.samearch.jira.lib.entity.mapper.impl.audit;

import org.samearch.jira.lib.entity.mapper.AuditJournalFilter;
import org.samearch.jira.lib.entity.mapper.DateRange;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AuditJournalFilterBuilder {

    private static final Long DEFAULT_EVENTS_COUNT = 100L;
    private static final ZonedDateTime MINIMAL_START_DATE;

    static {
        MINIMAL_START_DATE = ZonedDateTime.now()
                .withYear(1970)
                .withMonth(1)
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    private Long eventsCount;
    private final Set<Long> ids = new HashSet<>();
    private final Set<String> initiators = new HashSet<>();
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    public AuditJournalFilterBuilder withEventsLimit(Long eventsLimit) {
        this.eventsCount = eventsLimit;
        return this;
    }

    public AuditJournalFilterBuilder forIds(Long id) {
        if (id != null && id != 0) {
            this.ids.add(id);
        }
        return this;
    }

    public AuditJournalFilterBuilder forIds(List<Long> ids) {
        if (ids != null) {
            this.ids.addAll(ids);
        }
        return this;
    }

    public AuditJournalFilterBuilder byInitiator(String initiatorLogin) {
        if (initiatorLogin != null && !initiatorLogin.isEmpty()) {
            initiators.add(initiatorLogin);
        }
        return this;
    }

    public AuditJournalFilterBuilder byInitiator(Set<String> initiatorLogins) {
        if (initiatorLogins != null) {
            initiators.addAll(initiatorLogins);
        }
        return this;
    }

    public AuditJournalFilterBuilder startFromDate(ZonedDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public AuditJournalFilterBuilder beforeDate(ZonedDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public AuditJournalFilter build() {
        Long realEventsLimit = eventsCount != null && eventsCount != 0
                ? eventsCount
                : DEFAULT_EVENTS_COUNT;
        ZonedDateTime realStartDate = startDate != null
                ? startDate
                : MINIMAL_START_DATE;
        ZonedDateTime realEndDate = endDate != null
                ? endDate
                : ZonedDateTime.now();
        if (realStartDate.isAfter(realEndDate)) {
            throw new RuntimeException("Start date of interval can't be after end date of interval");
        }
        if (realStartDate.equals(realEndDate)) {
            realEndDate = realEndDate.plusDays(1);
        }
        DateRange dateRange = new DateRange(realStartDate, realEndDate);
        return new AuditJournalFilterImpl(realEventsLimit, ids, initiators, dateRange);
    }

    public AuditJournalFilter withoutFiltering() {
        return new AuditJournalFilterImpl(
                DEFAULT_EVENTS_COUNT,
                new HashSet<>(),
                new HashSet<>(),
                new DateRange(MINIMAL_START_DATE, ZonedDateTime.now())
        );
    }

    private static final class AuditJournalFilterImpl implements AuditJournalFilter {

        private final Long eventsCount;
        private final Set<Long> ids;
        private final Set<String> initiators;
        private final DateRange dateRange;

        public AuditJournalFilterImpl(Long eventsCount, Set<Long> ids, Set<String> initiators, DateRange dateRange) {
            this.eventsCount = eventsCount;
            this.ids = ids;
            this.initiators = initiators;
            this.dateRange = dateRange;
        }

        @Override
        public Long eventsCount() {
            return eventsCount;
        }

        @Override
        public Set<Long> forIds() {
            return new HashSet<>(ids);
        }

        @Override
        public Set<String> byInitiator() {
            return new HashSet<>(initiators);
        }

        @Override
        public DateRange inDateRange() {
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AuditJournalFilterImpl)) return false;

            AuditJournalFilterImpl that = (AuditJournalFilterImpl) o;

            if (!Objects.equals(ids, that.ids)) return false;
            if (!Objects.equals(initiators, that.initiators)) return false;
            return Objects.equals(dateRange, that.dateRange);
        }

        @Override
        public int hashCode() {
            int result = ids != null ? ids.hashCode() : 0;
            result = 31 * result + (initiators != null ? initiators.hashCode() : 0);
            result = 31 * result + (dateRange != null ? dateRange.hashCode() : 0);
            return result;
        }
    }


}
