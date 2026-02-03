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

package org.samearch.jira.lib.entity.mapper.impl.audit;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import org.samearch.jira.lib.entity.mapper.AuditEventRecord;
import org.samearch.jira.lib.entity.mapper.AuditJournal;
import org.samearch.jira.lib.entity.mapper.AuditJournalFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ExportAsService({AuditJournal.class})
public class DefaultAuditJournal implements AuditJournal {

    private final AuditRecordStorage auditRecordStorage;

    @Autowired
    public DefaultAuditJournal(AuditRecordStorage auditRecordStorage) {
        this.auditRecordStorage = auditRecordStorage;
    }

    @Override
    public List<AuditEventRecord> getEvents(AuditJournalFilter eventsFilter) {
        return auditRecordStorage.getRecords(eventsFilter.eventsCount().intValue());
    }

    @Override
    public AuditEventRecord createAuditEventRecord(AuditEventRecord eventRecord) {

        return auditRecordStorage.addAuditEvent(eventRecord);

    }

}
