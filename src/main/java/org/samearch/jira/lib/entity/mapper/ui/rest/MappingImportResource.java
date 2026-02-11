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

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.samearch.jira.lib.entity.mapper.api.EntityMapper;
import org.samearch.jira.lib.entity.mapper.api.EntityMapping;
import org.samearch.jira.lib.entity.mapper.api.exception.EntityMappingException;
import org.samearch.jira.lib.entity.mapper.ui.rest.dto.EntityMappingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.Set;

/**
 * Ресурс для поддержки импорта настроек маппинга.<br/>
 * <br/>
 * Умеет импортировать настройки маппинга, полученные через {@link MappingSettingsResource#getMappingsList}
 */
@Component
@Path("/import")
@Produces({MediaType.APPLICATION_JSON})
public class MappingImportResource {

    @ComponentImport
    private final JiraAuthenticationContext authenticationContext;

    private final EntityMapper entityMapper;
    private final EntityMappingRestUtils mappingUtils;

    @Autowired
    public MappingImportResource(EntityMapper entityMapper,
                                 EntityMappingRestUtils mappingUtils,
                                 JiraAuthenticationContext authenticationContext) {

        this.entityMapper = entityMapper;
        this.mappingUtils = mappingUtils;
        this.authenticationContext = authenticationContext;

    }

    /**
     * Предоставляет ресурс, позволяющий импортировать настройки маппингов.<br/>
     * <br/>
     * Импорт происходит по ключам. Идентификаторы маппингов (если они присутствуют) игнорируются.<br/>
     * Для маппингов с несуществующими ключами создаются новые записи.<br/>
     * Если для ключа уже существует маппинг, то его значение обновляется.<br/>
     * Существующие записи не удаляются.
     */
    @POST
    @Path("/")
    public Response importFromJson(Set<EntityMappingDto> importedMappings) {

        ApplicationUser currentUser = authenticationContext.getLoggedInUser();
        importedMappings.forEach(entityMappingDto -> {
            String mappingKey = entityMappingDto.getKey();
            String mappingValue = entityMappingDto.getValue();
            if (!entityMapper.isMappingPresent(mappingKey)) {
                try {
                    entityMapper.addMapping(currentUser, mappingKey, mappingValue);
                } catch (EntityMappingException ignore) {
                    /*
                     * Игнорируем ошибку создания маппинга: сейчас она генерируется только в том случае, когда для
                     * указанного ключа уже существует маппинг.
                     */
                }
            } else {
                Optional<EntityMapping> existedMappingHolder = entityMapper.getMapping(mappingKey);
                if (existedMappingHolder.isPresent()) {
                    try {
                        EntityMapping existedMapping = existedMappingHolder.get();
                        mappingUtils.updateEntityMappingFromRestRequest(currentUser, existedMapping.getId(), entityMappingDto);
                    } catch (EntityMappingException ignore) {
                        /*
                         * Игнорируем ошибку изменения маппинга: при обновлении ошибка генерируется только в том случае,
                         * когда маппинга с указанным идентификатором не существует. Учитывая вышестоящие проверки
                         * такая ситуация не возможна.
                         */
                    }
                }
            }
        });

        return Response.ok().build();

    }

}
