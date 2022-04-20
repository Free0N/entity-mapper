package org.samearch.jira.lib.entity.mapper;

/**
 * Список действий, доступных для записи маппинга, которые будут залогированы в журнал аудита.
 */
public enum EntityMappingEvent {

    CREATE,
    UPDATE,
    DELETE;

}
