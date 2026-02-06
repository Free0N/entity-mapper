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

import com.atlassian.jira.user.ApplicationUser;
import org.samearch.jira.lib.entity.mapper.AuditEventRecord;
import org.samearch.jira.lib.entity.mapper.AuditJournal;
import org.samearch.jira.lib.entity.mapper.AuditJournalFilter;
import org.samearch.jira.lib.entity.mapper.impl.audit.AuditJournalFilterBuilder;
import org.samearch.jira.lib.entity.mapper.ui.rest.dto.AuditEventRecordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Path("/audit")
@Produces({MediaType.APPLICATION_JSON})
public class AuditRecordsResources {

    private static final DateTimeFormatter REQUEST_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter RESPONSE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final AuditJournal auditJournal;
    private final EntityMappingRestUtils restUtils;

    @Autowired
    public AuditRecordsResources(AuditJournal auditJournal, EntityMappingRestUtils restUtils) {
        this.auditJournal = auditJournal;
        this.restUtils = restUtils;
    }

    @GET
    @Path("/records")
    public Response getAuditRecordsList(
            @QueryParam("mappingId") String mappingIdArg,
            @QueryParam("initiator") String initiatorLoginArg,
            @QueryParam("startDate") String startDateArg,
            @QueryParam("endDate") String endDateArg,
            @QueryParam("eventsLimit") Integer eventsLimit
    ) {
        AuditJournalFilterBuilder filterBuilder = new AuditJournalFilterBuilder();
        filterBuilder.withEventsLimit(eventsLimit != null ? eventsLimit : 50);
        if (mappingIdArg != null && !mappingIdArg.trim().isEmpty()) {
            filterBuilder.forIds(Long.parseLong(mappingIdArg));
        }
        if (initiatorLoginArg != null && !initiatorLoginArg.trim().isEmpty()) {
            filterBuilder.byInitiator(initiatorLoginArg);
        }
        if (startDateArg != null && !startDateArg.trim().isEmpty()) {
            ZonedDateTime startDate = argToZonedDateTime(startDateArg);
            filterBuilder.startFromDate(startDate);
        }
        if (endDateArg != null && !endDateArg.trim().isEmpty()) {
            ZonedDateTime endDate = argToZonedDateTime(endDateArg);
            filterBuilder.beforeDate(endDate);
        }
        AuditJournalFilter journalFilter = filterBuilder.build();
        List<AuditEventRecordDto> savedAuditRecords = auditJournal.getEvents(journalFilter).stream()
                .sorted(Comparator.comparing(AuditEventRecord::getDate))
                .map(this::objectToDto)
                .collect(Collectors.toList());

        return Response.ok(savedAuditRecords).build();
    }

    @GET
    @Path("/sys-admins")
    public Response getAdminUsers(
            @QueryParam("q") String filterString
    ) {
        List<ApplicationUser> filteredAdmins = restUtils.getAdminUsers(filterString);
        List<Map<String, String>> selectListData = new ArrayList<>();
        filteredAdmins.forEach(admin -> {
            Map<String, String> adminData = new HashMap<>();
            adminData.put("label", admin.getDisplayName());
            adminData.put("value", admin.getName());
            selectListData.add(adminData);
        });
        return Response.ok(selectListData).build();
    }

    private AuditEventRecordDto objectToDto(AuditEventRecord auditRecord) {

        AuditEventRecordDto auditRecordDto = new AuditEventRecordDto();

        auditRecordDto.setId(auditRecord.getId());
        auditRecordDto.setDate(RESPONSE_DATE_TIME_FORMATTER.format(auditRecord.getDate()));
        auditRecordDto.setInitiator(auditRecord.getInitiator().getName());
        auditRecordDto.setEvent(auditRecord.getEvent());
        auditRecordDto.setMappingId(auditRecord.getMappingId());
        auditRecordDto.setAdditionalInformation(auditRecord.getAdditionalInformation());

        return auditRecordDto;

    }

    private ZonedDateTime argToZonedDateTime(String arg) {
        return LocalDate.parse(arg, REQUEST_DATE_FORMATTER).atStartOfDay(ZoneId.systemDefault());
    }

}
