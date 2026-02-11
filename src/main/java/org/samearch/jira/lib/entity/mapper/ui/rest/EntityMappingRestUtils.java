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

package org.samearch.jira.lib.entity.mapper.ui.rest;

import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.apache.commons.lang3.StringUtils;
import org.samearch.jira.lib.entity.mapper.api.EntityMapper;
import org.samearch.jira.lib.entity.mapper.api.EntityMapping;
import org.samearch.jira.lib.entity.mapper.api.exception.EntityMappingConflictException;
import org.samearch.jira.lib.entity.mapper.api.exception.EntityMappingNotFoundException;
import org.samearch.jira.lib.entity.mapper.ui.rest.dto.EntityMappingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Набор высокоуровневых утилит, применяемых в реализациях REST endpoin-ов.
 */
@Component
class EntityMappingRestUtils {

    @ComponentImport
    private final GlobalPermissionManager globalPermissionManager;
    @ComponentImport
    private final GroupManager groupManager;

    private final EntityMapper entityMapper;

    @Autowired
    public EntityMappingRestUtils(GlobalPermissionManager globalPermissionManager, GroupManager groupManager, EntityMapper entityMapper) {
        this.globalPermissionManager = globalPermissionManager;
        this.groupManager = groupManager;
        this.entityMapper = entityMapper;
    }

    /**
     * Обновляет состояние маппинга на основании данных, содержащихся в {@code updateRequestData}.<br/>
     * <br/>
     * При обновлении состояния маппинга учитывается то, что ни ключ, ни значение маппинга не могут быть пустыми. Т.е.
     * если в {@code updateRequestData} какое-либо значение (для {@code key} или {@code value}) равно {@code null}, то
     * считается, что эта часть состояния маппинга не требует обновления.
     *
     * @param mappingId идентификатор обновляемого маппинга
     * @param updateRequestData данные для обновления ключа и/или значения маппинга
     * @return обновленное состояние маппинга
     * @throws EntityMappingNotFoundException в случае, когда маппинга с указанным идентификатором не существует
     * @throws EntityMappingConflictException в случае, когда обновляет ключ маппинга, а для нового ключа уже существует запись
     */
    public EntityMapping updateEntityMappingFromRestRequest(ApplicationUser user, int mappingId, EntityMappingDto updateRequestData)
            throws EntityMappingNotFoundException, EntityMappingConflictException {

        Optional<EntityMapping> currentEntityMappingHolder = entityMapper.getMapping(mappingId);
        if (!currentEntityMappingHolder.isPresent()) {
            throw new EntityMappingNotFoundException(mappingId);
        }

        final EntityMapping currentEntityMapping = currentEntityMappingHolder.get();
        final EntityMapping updatedEntityMappingData = constructUpdateMappingObject(currentEntityMapping, updateRequestData);

        return entityMapper.updateMapping(user.getKey(), mappingId, updatedEntityMappingData.getKey(), updatedEntityMappingData.getValue());

    }

    /**
     * Конструирует запись маппинга обновляя данные существующей записи.
     *
     * @param currentMappingState Существующая запись маппинга. Исходные данные конструируемой записи.
     * @param mappingUpdateRequestData Новые данные для существующей записи, пришедшие из REST-запроса или другого источника.
     */
    private EntityMapping constructUpdateMappingObject(
            final EntityMapping currentMappingState,
            final EntityMappingDto mappingUpdateRequestData) {

        EntityMapping updatedEntityMapping = new EntityMapping();
        updatedEntityMapping.setId(currentMappingState.getId());

        final String newMappingKey = mappingUpdateRequestData.getKey();
        if (StringUtils.isNotBlank(newMappingKey)) {
            updatedEntityMapping.setKey(newMappingKey);
        } else {
            updatedEntityMapping.setKey(currentMappingState.getKey());
        }

        final String newMappingValue = mappingUpdateRequestData.getValue();
        if (StringUtils.isNotBlank(newMappingValue)) {
            updatedEntityMapping.setValue(newMappingValue);
        } else {
            updatedEntityMapping.setValue(currentMappingState.getValue());
        }

        return updatedEntityMapping;
    }

    /**
     * Возвращает список пользователей, которые имеют полномочия SYSTEM_ADMIN или ADMINISTER.
     * @param filter будут возвращены только те пользователи, логин или имя которых начинается с указанной строки
     */
    public List<ApplicationUser> getAdminUsers(String filter, GlobalPermissionKey... permissions) {
        return Arrays.stream(permissions)
                .map(globalPermissionManager::getGroupsWithPermission)
                .flatMap(Collection::stream)
                .map(groupManager::getUsersInGroup)
                .flatMap(Collection::stream)
                .filter(user -> userDisplayAttributesStartsWith(user, filter))
                .collect(Collectors.toList());
    }

    private boolean userDisplayAttributesStartsWith(ApplicationUser user, String filter) {
        String displayName = user.getDisplayName();
        String name = user.getName();
        String email = user.getEmailAddress();
        return filter.trim().isEmpty()
                || displayName.startsWith(filter)
                || name.startsWith(filter)
                || email.startsWith(filter);
    }

    public EntityMappingDto objectToDto(final EntityMapping object) {
        EntityMappingDto dto = new EntityMappingDto();
        dto.setId(object.getId());
        dto.setKey(object.getKey());
        dto.setValue(object.getValue());
        return dto;
    }

}
