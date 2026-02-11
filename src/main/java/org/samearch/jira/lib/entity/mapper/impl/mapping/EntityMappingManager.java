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
import org.samearch.jira.lib.entity.mapper.exception.ClosedChainEntityMappingException;
import org.samearch.jira.lib.entity.mapper.exception.EntityMappingConflictException;
import org.samearch.jira.lib.entity.mapper.exception.EntityMappingNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/**
 * Высокоуровневый сервис управления записями маппинга.<br/>
 * <br/>
 * "Прозрачно" выполняет различные проверки и, в случае недопустимых ситуаций, генерирует соответствующие ошибки.
 */
@Component
public class EntityMappingManager {

    protected final EntityMappingStorage mappingStorage;
    private final MappingLinksResolver mappingLinksResolver;

    @Autowired
    public EntityMappingManager(EntityMappingStorage mappingStorage, MappingLinksResolver mappingLinksResolver) {

        this.mappingStorage = mappingStorage;
        this.mappingLinksResolver = mappingLinksResolver;

    }

    public EntityMapping addMapping(String key, String value) throws EntityMappingConflictException {

        EntityMapping alreadySavedMappingForKey = mappingStorage.getMappingForKey(key);

        if (alreadySavedMappingForKey != null) {
            throw new EntityMappingConflictException(key);
        } else {
            return mappingStorage.createEntityMapping(key, value);
        }

    }

    public void removeMapping(String key) {
        mappingStorage.deleteEntityMapping(key);
    }

    public EntityMapping updateMapping(int mappingId, String newMappingKey, String newMappingValue)
            throws EntityMappingNotFoundException, EntityMappingConflictException {

        EntityMapping currentEntityMapping = mappingStorage.getMappingById(mappingId);

        if (currentEntityMapping == null) {
            throw new EntityMappingNotFoundException(mappingId);
        } else {
            checkMappingConflictForKey(currentEntityMapping.getId(), newMappingKey);

            return mappingStorage.updateEntityMapping(mappingId, newMappingKey, newMappingValue);
        }

    }

    public Optional<String> getMappedValue(String key) throws ClosedChainEntityMappingException {

        String effectiveMappingKey = mappingLinksResolver.resolveTargetMappingKey(key);
        EntityMapping savedMapping = mappingStorage.getMappingForKey(effectiveMappingKey);

        return (savedMapping != null)
                ? Optional.ofNullable(savedMapping.getValue())
                : Optional.empty();

    }

    public <X extends Throwable> String getMappedValueOrElseThrow(String key, X exception) throws X, ClosedChainEntityMappingException {

        String effectiveMappingKey = mappingLinksResolver.resolveTargetMappingKey(key);
        EntityMapping savedMapping = mappingStorage.getMappingForKey(effectiveMappingKey);

        if (savedMapping != null) {
            return savedMapping.getValue();
        } else {
            throw exception;
        }

    }

    public Set<EntityMapping> getMappedValues() {
        return mappingStorage.getEntityMappings();
    }

    public Set<EntityMapping> getMappedValuesLike(String keyFilter) {
        return mappingStorage.getEntityMappingsLike(keyFilter);
    }

    public Optional<EntityMapping> getMapping(int mappingId) {
        return Optional.ofNullable(mappingStorage.getMappingById(mappingId));
    }

    public Optional<EntityMapping> getMapping(String mappingKey) {
        return Optional.ofNullable(mappingStorage.getMappingForKey(mappingKey));
    }

    public boolean isMappingPresent(String key) {
        return mappingStorage.getMappingForKey(key) != null;
    }

    /**
     * Проверяет, существует ли маппинг для ключа {@code newMappingKey} и имеет ли эта запись указанный идентификатор.
     * @throws EntityMappingConflictException в случае, когда для ключа {@code newMappingKey} существует маппинг и его
     *                                        идентификатор отличается от {@code expectedMappingId}
     */
    private void checkMappingConflictForKey(int expectedMappingId, String newMappingKey) throws EntityMappingConflictException {

        Optional<EntityMapping> mappingForNewKeyHolder = getMapping(newMappingKey);

        if (mappingForNewKeyHolder.isPresent()) {
            EntityMapping mappingForNewKey = mappingForNewKeyHolder.get();

            if (mappingForNewKey.getId() != expectedMappingId) {
                throw new EntityMappingConflictException(newMappingKey);
            }
        }

    }
}
