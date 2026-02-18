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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ресурс для поддержки CRUD-операций интерфейса управления маппингами.
 */
@Component
@Path("/mapping")
@Produces({MediaType.APPLICATION_JSON})
public class MappingResource {

    @ComponentImport
    private final JiraAuthenticationContext authenticationContext;

    private final EntityMapper entityMapper;
    private final EntityMappingRestUtils mappingUtils;

    @Autowired
    public MappingResource(EntityMapper entityMapper,
                           EntityMappingRestUtils mappingUtils,
                           JiraAuthenticationContext authenticationContext) {

        this.entityMapper = entityMapper;
        this.mappingUtils = mappingUtils;
        this.authenticationContext = authenticationContext;

    }

    @GET
    @Path("/")
    public Response getMappingsList() {

        Set<EntityMappingDto> savedMappings = entityMapper.getMappedValues().stream()
                .map(mappingUtils::objectToDto)
                .collect(Collectors.toSet());

        return Response.ok(savedMappings).build();

    }

    @GET
    @Path("/{mappingId}")
    public Response getMapping(@PathParam("mappingId") String mappingIdParam) {

        try {
            final int mappingId = Integer.parseInt(mappingIdParam);
            Optional<EntityMapping> savedMappingHolder = entityMapper.getMapping(mappingId);
            if (savedMappingHolder.isPresent()) {
                return Response.ok(mappingUtils.objectToDto(savedMappingHolder.get())).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    @POST
    @Path("/")
    public Response createMapping(final EntityMappingDto entityMappingDto) {

        ApplicationUser currentUser = authenticationContext.getLoggedInUser();
        try {
            // TODO: перенести проверку корректности заполнения данных в отдельный класс
            final String mappingKey = entityMappingDto.getKey();
            final String mappingValue = entityMappingDto.getValue();
            if (StringUtils.isEmpty(mappingKey) || StringUtils.isEmpty(mappingValue)) {
                ErrorMessage errorMessage = new ErrorMessage("Mapping key and value can not be empty.");
                return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
            }
            final EntityMapping createdMapping = entityMapper.addMapping(currentUser.getKey(), mappingKey, mappingValue);
            final EntityMappingDto createdMappingDto = mappingUtils.objectToDto(createdMapping);
            return Response.ok(createdMappingDto).build();
        } catch (EntityMappingException e) {
            return Response.status(Response.Status.CONFLICT).build();
        }

    }

    @DELETE
    @Path("/{mappingId}")
    public Response deleteMapping(@PathParam("mappingId") Integer mappingId) {
        ApplicationUser currentUser = authenticationContext.getLoggedInUser();
        entityMapper.getMapping(mappingId)
                .ifPresent(savedMapping -> entityMapper.removeMapping(currentUser.getKey(), savedMapping.getKey()));
        return Response.ok().build();
    }

    @PUT
    @Path("/{mappingId}")
    public Response updateMapping(@PathParam("mappingId") String mappingIdParam, final EntityMappingDto entityMappingDto) {

        ApplicationUser currentUser = authenticationContext.getLoggedInUser();

        try {
            final int mappingId = Integer.parseInt(mappingIdParam);
            Optional<EntityMapping> currentEntityMappingHolder = entityMapper.getMapping(mappingId);
            if (!currentEntityMappingHolder.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            EntityMapping updatedMapping = mappingUtils.updateEntityMappingFromRestRequest(currentUser, mappingId, entityMappingDto);
            EntityMappingDto updatedMappingDto = mappingUtils.objectToDto(updatedMapping);

            return Response.ok(updatedMappingDto).build();
        } catch (NumberFormatException e) {
            String errorMessageBody = String.format("Bad mapping identifier: %s", mappingIdParam);
            ErrorMessage errorMessage = new ErrorMessage(errorMessageBody);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
        } catch (EntityMappingNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (EntityMappingConflictException e) {
            ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
        }

    }
}
