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

package org.samearch.jira.lib.entity.mapper;

import com.atlassian.jira.user.ApplicationUser;
import org.samearch.jira.lib.entity.mapper.exception.ClosedChainEntityMappingException;
import org.samearch.jira.lib.entity.mapper.exception.EntityMappingConflictException;
import org.samearch.jira.lib.entity.mapper.exception.EntityMappingNotFoundException;

import java.util.Optional;
import java.util.Set;

/**
 * Предоставляет функционал для сопоставления различных сущностей/идентификаторов по ключам.
 */
public interface EntityMapper {

    /**
     * Создает новый маппинг для ключа.<br/>
     *
     * @throws EntityMappingConflictException в случае, когда для указанного ключа уже задано какое-либо значение
     */
    EntityMapping addMapping(ApplicationUser user, String key, String value) throws EntityMappingConflictException;

    /**
     * Удаляет маппинг с указанным ключом.
     */
    void removeMapping(ApplicationUser user, String key);

    /**
     * Обновляет параметры маппинга с указанным id.<br/>
     *
     * @param mappingId идентификатор маппинга, параметры которого нужно обновить
     * @param key новый ключ маппинга
     * @param newMappingValue новое значение для ключа маппинга
     * @return обновленное состояние маппинга
     * @throws EntityMappingNotFoundException если маппинг с указанным идентификатором не найден
     */
    EntityMapping updateMapping(ApplicationUser user, int mappingId, String key, String newMappingValue)
            throws EntityMappingNotFoundException, EntityMappingConflictException;

    /**
     * Возвращает список всех существующих маппингов.
     */
    Set<EntityMapping> getMappedValues();

    /**
     * Возвращает маппинг по идентификатору.<br/>
     * <br/>
     * Если маппинг с указанным идентификатором не существует, то в Optional будет лежать пустое значение.
     */
    Optional<EntityMapping> getMapping(int mappingId);

    /**
     * Возвращает маппинг по ключу.<br/>
     * Если для указанного ключа маппинга не существует, то в Optional будет лежать пустое значение.
     */
    Optional<EntityMapping> getMapping(String mappingKey);

    /**
     * Возвращает значение маппинга по ключу.<br/>
     * <br/>
     * Если для указанного ключа маппинга не существует, то в Optional будет лежать пустое значение.
     */
    Optional<String> getMappedValue(String key) throws ClosedChainEntityMappingException;

    /**
     * Возвращает значение маппинга по ключу.<br/>
     * <br/>
     * Если для указанного ключа маппинга не существует, то будет сгенерирована ошибка, переданная во втором параметре.
     */
    <X extends Throwable> String getMappedValueOrElseThrow(String key, X exception) throws X, ClosedChainEntityMappingException;

    /**
     * Позволяет проверить наличие маппинга для указанного ключа.
     */
    boolean isMappingPresent(String key);

}
