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
import org.apache.commons.lang3.StringUtils;
import org.samearch.jira.lib.entity.mapper.api.EntityMapper;
import org.samearch.jira.lib.entity.mapper.api.EntityMapping;
import org.samearch.jira.lib.entity.mapper.api.exception.EntityMappingConflictException;
import org.samearch.jira.lib.entity.mapper.api.exception.EntityMappingException;
import org.samearch.jira.lib.entity.mapper.api.exception.EntityMappingNotFoundException;
import org.samearch.jira.lib.entity.mapper.ui.rest.dto.EntityMappingDto;
import org.samearch.jira.lib.entity.mapper.ui.rest.dto.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ресурс для поддержки CRUD-операций интерфейса управления маппингами.
 */
@Component
@Path("/project")
@Produces({MediaType.APPLICATION_JSON})
public class ProjectMappingSettingsResource {

    @ComponentImport
    private final JiraAuthenticationContext authenticationContext;

    private final EntityMapper entityMapper;
    private final EntityMappingRestUtils mappingUtils;

    @Autowired
    public ProjectMappingSettingsResource(EntityMapper entityMapper,
                                          EntityMappingRestUtils mappingUtils,
                                          JiraAuthenticationContext authenticationContext) {

        this.entityMapper = entityMapper;
        this.mappingUtils = mappingUtils;
        this.authenticationContext = authenticationContext;
    }

    @GET
    @Path("/{projectKey}/mappings")
    public Response getMappingsList(@PathParam("projectKey") String projectKey) {
        String keysFilter = String.format("project.%s.", projectKey);
        return responseWithMapping(projectKey, entityMapper.getMappedValuesLike(keysFilter));
    }

    @GET
    @Path("/{projectKey}/mapping/{mappingId}")
    public Response getMapping(
            @PathParam("projectKey") String projectKey,
            @PathParam("mappingId") Integer mappingId
    ) {
        try {
            return getMappingInProject(projectKey, mappingId)
                    .map(it -> responseWithMapping(projectKey, it))
                    .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("/{projectKey}/mapping")
    public Response createMapping(
            @PathParam("projectKey") String projectKey,
            final EntityMappingDto entityMappingDto
    ) {
        ApplicationUser currentUser = authenticationContext.getLoggedInUser();
        final String mappingKey = entityMappingDto.getKey();
        final String mappingValue = entityMappingDto.getValue();
        // TODO: перенести проверку корректности заполнения данных в отдельный класс
        if (StringUtils.isEmpty(mappingKey) || StringUtils.isEmpty(mappingValue)) {
            ErrorMessage errorMessage = new ErrorMessage("Mapping key and value can not be empty.");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
        }
        final String actualMappingKey = buildActualEntityKey(projectKey, entityMappingDto.getKey());
        try {
            final EntityMapping createdMapping = entityMapper.addMapping(currentUser, actualMappingKey, mappingValue);
            return responseWithMapping(projectKey, createdMapping);
        } catch (EntityMappingException e) {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }

    @DELETE
    @Path("/{projectKey}/mapping/{mappingId}")
    public Response deleteMapping(
            @PathParam("projectKey") String projectKey,
            @PathParam("mappingId") Integer mappingId
    ) {
        ApplicationUser currentUser = authenticationContext.getLoggedInUser();
        getMappingInProject(projectKey, mappingId)
                .ifPresent(it -> entityMapper.removeMapping(currentUser, it.getKey()));
        return Response.ok().build();
    }

    @PUT
    @Path("/{projectKey}/mapping/{mappingId}")
    public Response tryToUpdateMapping(
            @PathParam("projectKey") String projectKey,
            @PathParam("mappingId") Integer mappingId,
            final EntityMappingDto entityMappingDto
    ) {
        ApplicationUser currentUser = authenticationContext.getLoggedInUser();
        return getMappingInProject(projectKey, mappingId)
                .map(it -> updateMapping(currentUser, projectKey, mappingId, entityMappingDto))
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    private Response updateMapping(ApplicationUser actor, String projectKey, Integer mappingId, EntityMappingDto mappingDto) {
        try {
            mappingDto.setKey(buildActualEntityKey(projectKey, mappingDto.getKey()));
            EntityMapping updatedMapping = mappingUtils.updateEntityMappingFromRestRequest(actor, mappingId, mappingDto);
            return responseWithMapping(projectKey, updatedMapping);
        } catch (EntityMappingNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (EntityMappingConflictException e) {
            ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
        }
    }

    private String buildActualEntityKey(String projectKey, String key) {
        if (key == null || key.trim().isEmpty()) {
            return key;
        }
        if (key.startsWith("project." + projectKey + ".")) {
            return key;
        }
        return String.format("project.%s.%s", projectKey, key);
    }

    private Optional<EntityMapping> getMappingInProject(String projectKey, Integer mappingId) {
        return entityMapper.getMapping(mappingId)
                .filter(it -> isKeyForProject(projectKey, it.getKey()));
    }

    private boolean isKeyForProject(String projectKey, String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        return key.startsWith("project." + projectKey + ".");
    }

    private Response responseWithMapping(String projectKey, Collection<EntityMapping> entityMappings) {
        Set<EntityMappingDto> entityMappingDtos = entityMappings.stream()
                .map(mappingUtils::objectToDto)
                .map(it -> removeProjectPrefixFromKey(projectKey, it))
                .collect(Collectors.toSet());
        return Response.ok(entityMappingDtos).build();
    }

    private Response responseWithMapping(String projectKey, EntityMapping entityMapping) {
        final EntityMappingDto mappingDto = mappingUtils.objectToDto(entityMapping);
        final EntityMappingDto projectTrimmedMappingDto = removeProjectPrefixFromKey(projectKey, mappingDto);
        return Response.ok(projectTrimmedMappingDto).build();
    }

    private EntityMappingDto removeProjectPrefixFromKey(String projectKey, EntityMappingDto mappingDto) {
        String trimmedMappingKey = mappingDto.getKey().replaceAll("^project\\." + projectKey + "\\.", "");
        EntityMappingDto trimmedProjectMappingDto = mappingDto.clone();
        trimmedProjectMappingDto.setKey(trimmedMappingKey);
        return trimmedProjectMappingDto;
    }

}
