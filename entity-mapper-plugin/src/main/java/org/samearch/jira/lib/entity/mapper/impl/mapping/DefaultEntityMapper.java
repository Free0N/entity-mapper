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

package org.samearch.jira.lib.entity.mapper.impl.mapping;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import org.samearch.jira.lib.entity.mapper.api.AuditEventRecord;
import org.samearch.jira.lib.entity.mapper.api.AuditJournal;
import org.samearch.jira.lib.entity.mapper.api.EntityMapper;
import org.samearch.jira.lib.entity.mapper.api.EntityMapping;
import org.samearch.jira.lib.entity.mapper.api.exception.ClosedChainEntityMappingException;
import org.samearch.jira.lib.entity.mapper.api.exception.EntityMappingConflictException;
import org.samearch.jira.lib.entity.mapper.api.exception.EntityMappingNotFoundException;
import org.samearch.jira.lib.entity.mapper.impl.audit.util.AuditRecordBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@ExportAsService({EntityMapper.class})
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
    public EntityMapping addMapping(String userKey, String key, String value) throws EntityMappingConflictException {
        EntityMapping createdEntityMapping = mappingManager.addMapping(key, value);
        AuditEventRecord auditEventRecord = auditRecordBuilder.buildRecordForCreateMappingEvent(userKey, createdEntityMapping);
        auditJournal.createAuditEventRecord(auditEventRecord);
        return createdEntityMapping;
    }

    @Override
    public void removeMapping(String userKey, String key) {
        Optional<EntityMapping> savedMappingHolder = mappingManager.getMapping(key);
        if (savedMappingHolder.isPresent()) {
            EntityMapping savedMapping = savedMappingHolder.get();
            AuditEventRecord auditEventRecord = auditRecordBuilder.buildRecordForDeleteMappingEvent(userKey, savedMapping);
            auditJournal.createAuditEventRecord(auditEventRecord);
            mappingManager.removeMapping(key);
        }
    }

    @Override
    public EntityMapping updateMapping(String userKey, int mappingId, String key, String newMappingValue) throws EntityMappingNotFoundException, EntityMappingConflictException {
        Optional<EntityMapping> currentMappingRecordHolder = mappingManager.getMapping(mappingId);
        if (!currentMappingRecordHolder.isPresent()) {
            throw new EntityMappingNotFoundException(mappingId);
        }
        EntityMapping currentMappingRecord = currentMappingRecordHolder.get();
        EntityMapping updatedMappingRecord = mappingManager.updateMapping(mappingId, key, newMappingValue);
        AuditEventRecord auditEventRecord = auditRecordBuilder.buildRecordForUpdateMappingEvent(userKey, currentMappingRecord, updatedMappingRecord);
        auditJournal.createAuditEventRecord(auditEventRecord);
        return updatedMappingRecord;
    }

    @Override
    public Set<EntityMapping> getMappedValues() {
        return mappingManager.getMappedValues();
    }

    @Override
    public Set<EntityMapping> getMappedValuesLike(String keyFilter) {
        return mappingManager.getMappedValuesLike(keyFilter);
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
