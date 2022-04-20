package org.samearch.jira.lib.entity.mapper.impl.audit.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import net.java.ao.Query;
import org.samearch.jira.lib.entity.mapper.AuditEventRecord;
import org.samearch.jira.lib.entity.mapper.impl.audit.AuditRecordStorage;
import org.samearch.jira.lib.entity.mapper.impl.audit.dao.util.QueryParametersExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Вынести код управления дополнительной информацией для записи аудита в отдельный класс-хелпер
 */
@Component
public class DefaultAuditRecordStorage implements AuditRecordStorage {

    @ComponentImport
    private final ActiveObjects ao;
    @ComponentImport
    private final UserManager userManager;

    private final QueryParametersExtractor queryParametersBuilder;

    @Autowired
    public DefaultAuditRecordStorage(ActiveObjects ao, UserManager userManager, QueryParametersExtractor queryParametersBuilder) {

        this.ao = ao;
        this.userManager = userManager;

        this.queryParametersBuilder = queryParametersBuilder;

    }

    @Override
    public AuditEventRecord addAuditEvent(AuditEventRecord auditEventRecord) {

        Map<String, Object> createdEntityFields = queryParametersBuilder.buildCreationRequestParameters(auditEventRecord);

        AuditEventEntity eventEntity = ao.create(AuditEventEntity.class, createdEntityFields);
        saveAdditionalInfoForEventRecord(auditEventRecord, eventEntity);

        return entityToObject(eventEntity);

    }

    @Override
    public List<AuditEventRecord> getLastRecords(int recordsCount) {

        Query selectQuery = Query.select("ID, INITIATOR, DATE, EVENT, MAPPING_ID")
                .from(AuditEventEntity.class)
                .order("DATE DESC")
                .limit(recordsCount);

        List<AuditEventRecord> eventRecords = new ArrayList<>();

        AuditEventEntity[] eventEntities = ao.find(AuditEventEntity.class, selectQuery);
        Arrays.stream(eventEntities).forEach(eventEntity -> eventRecords.add(entityToObject(eventEntity)));

        return eventRecords;

    }

    private void saveAdditionalInfoForEventRecord(AuditEventRecord auditEventRecord, AuditEventEntity eventEntity) {

        auditEventRecord.getAdditionalInformation().forEach((key, value) -> {

            Map<String, Object> additionalInfoRecordParam = new HashMap<>();

            additionalInfoRecordParam.put("KEY", key);
            additionalInfoRecordParam.put("VALUE", value);
            additionalInfoRecordParam.put("AUDIT_EVENT_ENTITY_ID", eventEntity.getId());

            ao.create(AuditEventAdditionalInfoEntity.class, additionalInfoRecordParam);

        });

    }

    private AuditEventRecord entityToObject(AuditEventEntity eventEntity) {

        if (eventEntity == null) {
            return null;
        }

        AuditEventRecord eventRecord = new AuditEventRecord();

        eventRecord.setId(eventEntity.getId());
        eventRecord.setInitiator(userManager.getUserByName(eventEntity.getInitiator()));
        eventRecord.setEvent(eventEntity.getEvent());
        eventRecord.setMappingId(eventEntity.getMappingId());

        Map<String, String> eventAdditionalInfo = queryParametersBuilder.extractAdditionalInfoFromEventEntity(eventEntity);
        eventRecord.setAdditionalInformation(eventAdditionalInfo);

        ZonedDateTime eventTimeWithZone = ZonedDateTime.ofInstant(eventEntity.getDate().toInstant(), ZoneId.systemDefault());
        eventRecord.setDate(eventTimeWithZone);

        return eventRecord;

    }

}
