package org.samearch.jira.lib.entity.mapper.impl;

import org.samearch.jira.lib.entity.mapper.EntityMapping;
import org.samearch.jira.lib.entity.mapper.impl.mapping.EntityMappingStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InMemoryEntityMappingStorage implements EntityMappingStorage {
    private int entitiesCounter = 0;
    private final HashMap<String, EntityMapping> mappingsByKey = new HashMap<>(100);
    private final HashMap<Integer, EntityMapping> mappingsById = new HashMap<>(100);

    @Override
    public synchronized EntityMapping createEntityMapping(String key, String value) {
        EntityMapping createdEntityMapping = new EntityMapping();

        createdEntityMapping.setId(entitiesCounter++);
        createdEntityMapping.setKey(key);
        createdEntityMapping.setValue(value);

        mappingsById.put(createdEntityMapping.getId(), createdEntityMapping);
        mappingsByKey.put(createdEntityMapping.getKey(), createdEntityMapping);

        return createdEntityMapping;
    }

    @Override
    public void deleteEntityMapping(String key) {
        EntityMapping savedMapping = mappingsByKey.get(key);
        if (savedMapping != null) {
            mappingsByKey.remove(key);
            mappingsById.remove(savedMapping.getId());
        }
    }

    @Override
    public EntityMapping updateEntityMapping(int mappingId, String newMappingKey, String newMappingValue) {
        EntityMapping savedMapping = mappingsById.get(mappingId);

        if (savedMapping != null) {
            String oldMappingKey = savedMapping.getKey();

            savedMapping.setKey(newMappingKey);
            savedMapping.setValue(newMappingValue);

            mappingsByKey.remove(oldMappingKey);
            mappingsByKey.put(newMappingKey, savedMapping);
        }
        return getMappingById(mappingId);
    }

    @Override
    public Set<EntityMapping> getEntityMappings() {
        return new HashSet<>(mappingsByKey.values());
    }

    @Override
    public EntityMapping getMappingForKey(String key) {
        return mappingsByKey.get(key);
    }

    @Override
    public EntityMapping getMappingById(int mappingId) {
        return mappingsById.get(mappingId);
    }
}
