package org.samearch.jira.lib.entity.mapper.impl.mapping.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import net.java.ao.Query;
import org.samearch.jira.lib.entity.mapper.EntityMapping;
import org.samearch.jira.lib.entity.mapper.impl.mapping.EntityMappingStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DefaultEntityMappingStorage implements EntityMappingStorage {

    @ComponentImport
    private final ActiveObjects ao;

    @Autowired
    public DefaultEntityMappingStorage(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    public EntityMapping createEntityMapping(String key, String value) {

        Map<String, Object> createdEntityFields = buildCreationRequestParameters(key, value);

        EntityMappingEntity createdEntityMapping = ao.create(EntityMappingEntity.class, createdEntityFields);

        return entityToObject(createdEntityMapping);

    }

    private Map<String, Object> buildCreationRequestParameters(String key, String value) {

        Map<String, Object> createdEntityFields = new HashMap<>();

        createdEntityFields.put("KEY", key);
        createdEntityFields.put("VALUE", value);

        return createdEntityFields;

    }

    @Override
    public void deleteEntityMapping(String key) {

        EntityMappingEntity savedMappingForKey = findMappingByKey(key);

        if (savedMappingForKey != null) {
            ao.delete(savedMappingForKey);
        }

    }

    @Override
    public EntityMapping updateEntityMapping(int mappingId, String newMappingKey, String newMappingValue) {

        ao.executeInTransaction(() -> {
            EntityMappingEntity currentSavedMappingEntity = ao.get(EntityMappingEntity.class, mappingId);

            if (currentSavedMappingEntity != null) {
                updatedRecordFields(currentSavedMappingEntity, newMappingKey, newMappingValue);
            }

            return null;
        });

        return getMappingById(mappingId);

    }

    private void updatedRecordFields(EntityMappingEntity updatedMappingEntity, String newMappingKey, String newMappingValue) {

        updatedMappingEntity.setKey(newMappingKey);
        updatedMappingEntity.setValue(newMappingValue);
        updatedMappingEntity.save();

    }

    @Override
    public Set<EntityMapping> getEntityMappings() {

        EntityMappingEntity[] mappingEntities = ao.find(EntityMappingEntity.class);
        return Arrays.stream(mappingEntities)
                .map(this::entityToObject)
                .collect(Collectors.toSet());

    }

    @Override
    public EntityMapping getMappingForKey(String key) {

        EntityMappingEntity savedMappingForKey = findMappingByKey(key);
        return (savedMappingForKey != null)
                ? entityToObject(savedMappingForKey)
                : null;

    }

    @Override
    public EntityMapping getMappingById(int mappingId) {
        return entityToObject(ao.get(EntityMappingEntity.class, mappingId));
    }

    private EntityMappingEntity findMappingByKey(String key) {

        Query mappingSearchQuery = buildQueryForFindMappingByKey(key);

        EntityMappingEntity[] savedMappingsForKey = ao.find(EntityMappingEntity.class, mappingSearchQuery);

        if (savedMappingsForKey.length > 0) {
            return savedMappingsForKey[0];
        } else {
            return null;
        }

    }

    private Query buildQueryForFindMappingByKey(String key) {

        String mappingSearchQueryWhereClause = "KEY = ?";
        return Query.select().where(mappingSearchQueryWhereClause, key);

    }

    private EntityMapping entityToObject(EntityMappingEntity entity) {

        if (entity == null) {
            return null;
        }
        EntityMapping object = new EntityMapping();
        object.setId(entity.getID());
        object.setKey(entity.getKey());
        object.setValue(entity.getValue());
        return object;

    }

}
