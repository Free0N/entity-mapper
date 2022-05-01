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

import org.samearch.jira.lib.entity.mapper.AuditEventRecord;
import org.samearch.jira.lib.entity.mapper.AuditJournal;
import org.samearch.jira.lib.entity.mapper.ui.rest.dto.AuditEventRecordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/audit")
@Produces({MediaType.APPLICATION_JSON})
public class AuditRecordsResources {

    private final AuditJournal auditJournal;

    @Autowired
    public AuditRecordsResources(AuditJournal auditJournal) {
        this.auditJournal = auditJournal;
    }

    @GET
    @Path("/records")
    public Response getAuditRecordsList() {

        List<AuditEventRecordDto> savedAuditRecords = auditJournal.getLastEvents(50).stream()
                .sorted(Comparator.comparing(AuditEventRecord::getDate))
                .map(this::objectToDto)
                .collect(Collectors.toList());

        return Response.ok(savedAuditRecords).build();

    }

    private AuditEventRecordDto objectToDto(AuditEventRecord auditRecord) {

        AuditEventRecordDto auditRecordDto = new AuditEventRecordDto();

        auditRecordDto.setId(auditRecord.getId());
        auditRecordDto.setDate(formatDateTime(auditRecord.getDate()));
        auditRecordDto.setInitiator(auditRecord.getInitiator().getName());
        auditRecordDto.setEvent(auditRecord.getEvent());
        auditRecordDto.setMappingId(auditRecord.getMappingId());
        auditRecordDto.setAdditionalInformation(auditRecord.getAdditionalInformation());

        return auditRecordDto;

    }

    private String formatDateTime(ZonedDateTime dateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss MM.dd.yyyy");
        return formatter.format(dateTime);

    }

}
