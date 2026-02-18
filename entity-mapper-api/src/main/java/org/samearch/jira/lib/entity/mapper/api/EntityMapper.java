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

package org.samearch.jira.lib.entity.mapper.api;

import org.samearch.jira.lib.entity.mapper.api.exception.ClosedChainEntityMappingException;
import org.samearch.jira.lib.entity.mapper.api.exception.EntityMappingConflictException;
import org.samearch.jira.lib.entity.mapper.api.exception.EntityMappingNotFoundException;

import java.util.Optional;
import java.util.Set;

/**
 * Предоставляет функционал для сопоставления различных сущностей/идентификаторов по ключам.
 */
public interface EntityMapper {

    /**
     * Создает новый маппинг для ключа.
     *
     * @param userId идентификатор пользователя, который добавляет новый маппинг
     * @param key ключ создаваемого маппинга
     * @param value значение создаваемого маппинга
     * @return объект-представление записанного маппинга
     *
     * @throws EntityMappingConflictException в случае, когда для указанного ключа уже задано какое-либо значение
     */
    EntityMapping addMapping(String userId, String key, String value) throws EntityMappingConflictException;

    /**
     * Удаляет маппинг с указанным ключом.
     *
     * @param userId идентификатор пользователя, который удаляет маппинг
     * @param key ключ удаляемого маппинга
     */
    void removeMapping(String userId, String key);

    /**
     * Обновляет параметры маппинга с указанным id.
     *
     * @param userId идентификатор пользователя, который обновляет маппинг
     * @param mappingId идентификатор маппинга, параметры которого нужно обновить
     * @param key новый ключ маппинга
     * @param newMappingValue новое значение для ключа маппинга
     * @return обновленное состояние маппинга
     *
     * @throws EntityMappingNotFoundException если маппинг с указанным идентификатором не найден
     * @throws EntityMappingConflictException если ключ ключ маппинга был обновлен, и новое значение ключ уже
     *                                        используется в другом маппинге
     */
    EntityMapping updateMapping(String userId, int mappingId, String key, String newMappingValue)
            throws EntityMappingNotFoundException, EntityMappingConflictException;

    /**
     * Возвращает список всех существующих маппингов.
     *
     * @return список всех маппингов
     */
    Set<EntityMapping> getMappedValues();

    /**
     * Возвращает список существующих маппингов, ключи которых начинаются на указанный фильтр.
     *
     * @param keyFilter строка, с которой должен начинаться ключ маппинга
     * @return список маппингов, ключи которых начинаются с указанной строки
     */
    Set<EntityMapping> getMappedValuesLike(String keyFilter);

    /**
     * Возвращает маппинг по идентификатору.
     *
     * @param mappingId идентификатор запрашиваемого маппинга
     * @return если маппинга с указанным идентификатором не существует, то будет возвращен {@link Optional#empty()};
     *         если маппинг существует, то он будет обернут в {@link Optional}
     */
    Optional<EntityMapping> getMapping(int mappingId);

    /**
     * Возвращает маппинг по ключу.
     *
     * @param mappingKey ключ запрашиваемого маппинга
     * @return если маппинга с указанным ключем не существует, то будет возвращен {@link Optional#empty()};
     *         если маппинг существует, то он будет обернут в {@link Optional}
     */
    Optional<EntityMapping> getMapping(String mappingKey);

    /**
     * Возвращает значение маппинга по ключу.
     *
     * @param key ключ запрашиваемого маппинга
     * @return если маппинга с указанным ключем не существует, то будет возвращен {@link Optional#empty()};
     *         если маппинг существует, то его значение будет обернуто в {@link Optional}
     *
     * @throws ClosedChainEntityMappingException генерируется в случае обнаружения циклических ссылок между маппингами
     */
    Optional<String> getMappedValue(String key) throws ClosedChainEntityMappingException;

    /**
     * Возвращает значение маппинга по ключу.
     *
     * @param key ключ запрашиваемого маппинга
     * @param exception исключение, которое должно быть сгенерировано в случае отсутствия маппинга с указанным ключем
     * @param <X> класс исключения, которое будет сгенерировано в случае отсутствия маппинга с указанным ключем
     * @return значение маппинга с указанным ключем
     *
     * @throws ClosedChainEntityMappingException генерируется в случае обнаружения циклических ссылок между маппингами
     * @throws X в случае, если маппинг с указанным ключем не существует
     */
    <X extends Throwable> String getMappedValueOrElseThrow(String key, X exception) throws X, ClosedChainEntityMappingException;

    /**
     * Позволяет проверить наличие маппинга для указанного ключа.
     *
     * @param key ключ маппинга, наличие которого нужно проверить
     * @return true - если маппинг с указанным ключем существует;
     *         false - если маппинга с указанным ключем не существует
     */
    boolean isMappingPresent(String key);

}
