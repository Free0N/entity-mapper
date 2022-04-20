package org.samearch.jira.lib.entity.mapper.impl.audit;

import org.samearch.jira.lib.entity.mapper.AuditEventRecord;

import java.util.List;

public interface AuditRecordStorage {

    AuditEventRecord addAuditEvent(AuditEventRecord auditEventRecord);
    List<AuditEventRecord> getLastRecords(int recordsCount);

}
