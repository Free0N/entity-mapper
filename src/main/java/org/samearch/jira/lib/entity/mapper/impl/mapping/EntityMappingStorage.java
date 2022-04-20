package org.samearch.jira.lib.entity.mapper.impl.mapping;

import org.samearch.jira.lib.entity.mapper.EntityMapping;

import java.util.Set;

/**
 * Хранилище записей о маппинге.<br/>
 * Не выполняет никаких проверок. Простая реализация операций CRUD.
 */
public interface EntityMappingStorage {

    EntityMapping createEntityMapping(String key, String value);
    void deleteEntityMapping(String key);
    EntityMapping updateEntityMapping(int mappingId, String newMappingKey, String newMappingValue);
    Set<EntityMapping> getEntityMappings();
    EntityMapping getMappingForKey(String key);
    EntityMapping getMappingById(int mappingId);

}
