package org.samearch.jira.lib.entity.mapper;

import java.util.Set;

public interface AuditJournalFilter {
    Integer eventsCount();
    Set<Long> forIds();
    Set<String> byInitiator();
    DateRange inDateRange();
}
