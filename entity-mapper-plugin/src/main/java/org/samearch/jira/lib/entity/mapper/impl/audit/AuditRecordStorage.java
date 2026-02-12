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

import org.samearch.jira.lib.entity.mapper.api.AuditEventRecord;
import org.samearch.jira.lib.entity.mapper.api.AuditJournalFilter;

import java.util.List;

public interface AuditRecordStorage {

    AuditEventRecord addAuditEvent(AuditEventRecord auditEventRecord);
    List<AuditEventRecord> getRecords(AuditJournalFilter filter);

}
