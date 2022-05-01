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
