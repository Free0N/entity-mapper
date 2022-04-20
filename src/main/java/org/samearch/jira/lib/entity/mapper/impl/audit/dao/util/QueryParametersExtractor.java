package org.samearch.jira.lib.entity.mapper.impl.audit.dao.util;

import org.samearch.jira.lib.entity.mapper.AuditEventRecord;
import org.samearch.jira.lib.entity.mapper.impl.audit.dao.AuditEventAdditionalInfoEntity;
import org.samearch.jira.lib.entity.mapper.impl.audit.dao.AuditEventEntity;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class QueryParametersExtractor {

    public Map<String, Object> buildCreationRequestParameters(AuditEventRecord eventRecord) {

        Map<String, Object> createdEntityFields = new HashMap<>();

        createdEntityFields.put("INITIATOR", eventRecord.getInitiator().getName());
        createdEntityFields.put("EVENT", eventRecord.getEvent());
        createdEntityFields.put("MAPPING_ID", eventRecord.getMappingId());

        Date simpleEventDate = Date.from(eventRecord.getDate().toInstant());
        createdEntityFields.put("DATE", simpleEventDate);

        return createdEntityFields;

    }

    public Map<String, String> extractAdditionalInfoFromEventEntity(AuditEventEntity eventEntity) {

        Map<String, String> eventAdditionalInfo = new HashMap<>();

        if (eventEntity != null) {

            AuditEventAdditionalInfoEntity[] eventAdditionalInfoEntities = eventEntity.getEventAdditionalInfo();

            if (eventAdditionalInfoEntities != null) {
                Arrays.stream(eventAdditionalInfoEntities)
                        .map(this::eventAdditionalInfoEntityToObject)
                        .filter(Objects::nonNull)
                        .forEach(entry -> eventAdditionalInfo.put(entry.getKey(), entry.getValue()));
            }

        }

        return eventAdditionalInfo;

    }

    private Map.Entry<String, String> eventAdditionalInfoEntityToObject(AuditEventAdditionalInfoEntity additionalInfoEntity) {

        if (additionalInfoEntity == null) {
            return null;
        }

        String key = additionalInfoEntity.getKey();
        String value = additionalInfoEntity.getValue();

        return new AbstractMap.SimpleEntry<>(key, value);

    }

}
