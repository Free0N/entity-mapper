/*
 * This file is part of Entity Mapper Plugin.
 *
 * Entity Mapper Plugin is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Entity Mapper Plugin is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Entity Mapper Plugin.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2022 samearch.org
 */

package org.samearch.jira.lib.entity.mapper.impl;

import org.samearch.jira.lib.entity.mapper.EntityMapping;
import org.samearch.jira.lib.entity.mapper.impl.mapping.EntityMappingStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    public Set<EntityMapping> getEntityMappingsLike(String keyFilter) {
        return mappingsByKey.values().stream()
                .filter(mapping -> mapping.getKey().startsWith(keyFilter))
                .collect(Collectors.toSet());
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
