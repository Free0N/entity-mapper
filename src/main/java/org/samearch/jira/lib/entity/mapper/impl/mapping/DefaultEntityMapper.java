package org.samearch.jira.lib.entity.mapper.impl.mapping;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import org.samearch.jira.lib.entity.mapper.AuditEventRecord;
import org.samearch.jira.lib.entity.mapper.AuditJournal;
import org.samearch.jira.lib.entity.mapper.EntityMapper;
import org.samearch.jira.lib.entity.mapper.EntityMapping;
import org.samearch.jira.lib.entity.mapper.exception.ClosedChainEntityMappingException;
import org.samearch.jira.lib.entity.mapper.exception.EntityMappingConflictException;
import org.samearch.jira.lib.entity.mapper.exception.EntityMappingNotFoundException;
import org.samearch.jira.lib.entity.mapper.impl.audit.util.AuditRecordBuilder;

import java.util.Optional;
import java.util.Set;

@ExportAsService
public class DefaultEntityMapper implements EntityMapper {

    private final EntityMappingManager mappingManager;
    private final AuditJournal auditJournal;
    private final AuditRecordBuilder auditRecordBuilder;

    public DefaultEntityMapper(
            EntityMappingManager entityMappingManager,
            AuditJournal auditJournal,
            AuditRecordBuilder auditRecordBuilder) {

        this.mappingManager = entityMappingManager;
        this.auditJournal = auditJournal;
        this.auditRecordBuilder = auditRecordBuilder;

    }

    @Override
    public EntityMapping addMapping(ApplicationUser user, String key, String value) throws EntityMappingConflictException {

        EntityMapping createdEntityMapping = mappingManager.addMapping(key, value);

        AuditEventRecord auditEventRecord = auditRecordBuilder.buildRecordForCreateMappingEvent(user, createdEntityMapping);
        auditJournal.createAuditEventRecord(auditEventRecord);

        return createdEntityMapping;

    }

    @Override
    public void removeMapping(ApplicationUser user, String key) {

        Optional<EntityMapping> savedMappingHolder = mappingManager.getMapping(key);
        if (savedMappingHolder.isPresent()) {
            EntityMapping savedMapping = savedMappingHolder.get();

            AuditEventRecord auditEventRecord = auditRecordBuilder.buildRecordForDeleteMappingEvent(user, savedMapping);
            auditJournal.createAuditEventRecord(auditEventRecord);

            mappingManager.removeMapping(key);
        }

    }

    @Override
    public EntityMapping updateMapping(ApplicationUser user, int mappingId, String key, String newMappingValue) throws EntityMappingNotFoundException, EntityMappingConflictException {

        Optional<EntityMapping> currentMappingRecordHolder = mappingManager.getMapping(mappingId);
        if (!currentMappingRecordHolder.isPresent()) {
            throw new EntityMappingNotFoundException(mappingId);
        }

        EntityMapping currentMappingRecord = currentMappingRecordHolder.get();
        EntityMapping updatedMappingRecord = mappingManager.updateMapping(mappingId, key, newMappingValue);

        AuditEventRecord auditEventRecord = auditRecordBuilder.buildRecordForUpdateMappingEvent(user, currentMappingRecord, updatedMappingRecord);
        auditJournal.createAuditEventRecord(auditEventRecord);

        return updatedMappingRecord;

    }

    @Override
    public Set<EntityMapping> getMappedValues() {
        return mappingManager.getMappedValues();
    }

    @Override
    public Optional<EntityMapping> getMapping(int mappingId) {
        return mappingManager.getMapping(mappingId);
    }

    @Override
    public Optional<EntityMapping> getMapping(String mappingKey) {
        return mappingManager.getMapping(mappingKey);
    }

    @Override
    public Optional<String> getMappedValue(String key) throws ClosedChainEntityMappingException {
        return mappingManager.getMappedValue(key);
    }

    @Override
    public <X extends Throwable> String getMappedValueOrElseThrow(String key, X exception) throws X, ClosedChainEntityMappingException {
        return mappingManager.getMappedValueOrElseThrow(key, exception);
    }

    @Override
    public boolean isMappingPresent(String key) {
        return mappingManager.isMappingPresent(key);
    }
}
