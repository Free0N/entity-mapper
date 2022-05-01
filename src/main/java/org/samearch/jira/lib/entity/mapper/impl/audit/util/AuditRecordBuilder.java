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

package org.samearch.jira.lib.entity.mapper.impl.audit.util;

import com.atlassian.jira.user.ApplicationUser;
import org.samearch.jira.lib.entity.mapper.AuditEventRecord;
import org.samearch.jira.lib.entity.mapper.EntityMapping;
import org.samearch.jira.lib.entity.mapper.EntityMappingEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Вспомогательный класс для создания записей журнала аудита.
 */
@Component
public class AuditRecordBuilder {

    public AuditEventRecord buildRecordForCreateMappingEvent(@Nonnull ApplicationUser initiator,
                                                             @Nonnull EntityMapping entityMapping) {

        Map<String, String> additionalInfo = buildRecordAdditionalInfo(entityMapping);
        return buildBasicJournalRecord(initiator, EntityMappingEvent.CREATE, entityMapping, additionalInfo);

    }

    public AuditEventRecord buildRecordForDeleteMappingEvent(@Nonnull ApplicationUser initiator,
                                                             @Nonnull EntityMapping removedMapping) {

        Map<String, String> additionalInfo = buildRecordAdditionalInfo(removedMapping);
        return buildBasicJournalRecord(initiator, EntityMappingEvent.DELETE, removedMapping, additionalInfo);

    }

    public AuditEventRecord buildRecordForUpdateMappingEvent(@Nonnull ApplicationUser initiator,
                                                             @Nonnull EntityMapping currentMappingRecord,
                                                             @Nonnull EntityMapping updatedMappingRecord) {

        Map<String, String> additionalInformation = new HashMap<>();

        additionalInformation.putAll(buildRecordAdditionalInfoWithKeyPrefix(currentMappingRecord, "oldMapping."));
        additionalInformation.putAll(buildRecordAdditionalInfoWithKeyPrefix(updatedMappingRecord, "newMapping."));

        return buildBasicJournalRecord(initiator, EntityMappingEvent.UPDATE, currentMappingRecord, additionalInformation);

    }

    private Map<String, String> buildRecordAdditionalInfo(EntityMapping entityMapping) {

        Map<String, String> additionalInfo = new HashMap<>();

        additionalInfo.put("key", entityMapping.getKey());
        additionalInfo.put("value", entityMapping.getValue());

        return additionalInfo;

    }

    private Map<String, String> buildRecordAdditionalInfoWithKeyPrefix(EntityMapping entityMapping, String keyPrefix) {

        Map<String, String> additionalInfo = new HashMap<>();

        additionalInfo.put(keyPrefix + "key", entityMapping.getKey());
        additionalInfo.put(keyPrefix + "value", entityMapping.getValue());

        return additionalInfo;

    }

    private AuditEventRecord buildBasicJournalRecord(ApplicationUser initiator,
                                                     EntityMappingEvent event,
                                                     EntityMapping relatedMapping,
                                                     Map<String, String> additionalInformation) {

        AuditEventRecord eventJournalRecord = new AuditEventRecord();

        eventJournalRecord.setInitiator(initiator);
        eventJournalRecord.setEvent(event);
        eventJournalRecord.setDate(ZonedDateTime.now());
        eventJournalRecord.setMappingId(relatedMapping.getId());
        eventJournalRecord.setAdditionalInformation(additionalInformation);

        return eventJournalRecord;

    }

}
