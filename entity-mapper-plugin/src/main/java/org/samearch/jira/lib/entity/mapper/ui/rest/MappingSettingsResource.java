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

import org.samearch.jira.lib.entity.mapper.PluginSettingsManager;
import org.samearch.jira.lib.entity.mapper.ui.rest.dto.MappingSettingsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Ресурс для поддержки CRUD-операций интерфейса настроек маппингаов.
 */
@Component
@Path("/settings")
@Produces({MediaType.APPLICATION_JSON})
public class MappingSettingsResource {

    private final PluginSettingsManager pluginSettingsManager;

    @Autowired
    public MappingSettingsResource(PluginSettingsManager pluginSettingsManager) {
        this.pluginSettingsManager = pluginSettingsManager;
    }

    @GET
    @Path("/")
    public Response getMappingSettings() {
        return Response.ok(pluginSettingsManager.getMappingSettings()).build();
    }

    @PUT
    @Path("/")
    public Response updateMappingSettings(final MappingSettingsDto mappingSettings) {
        pluginSettingsManager.updatePluginSettings(mappingSettings);
        return Response.ok().build();
    }

}
