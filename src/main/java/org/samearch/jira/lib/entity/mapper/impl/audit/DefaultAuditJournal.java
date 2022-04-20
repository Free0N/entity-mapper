package org.samearch.jira.lib.entity.mapper.impl.audit;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import org.samearch.jira.lib.entity.mapper.AuditEventRecord;
import org.samearch.jira.lib.entity.mapper.AuditJournal;
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
    public List<AuditEventRecord> getLastEvents(int lastEventsCount) {
        return auditRecordStorage.getLastRecords(lastEventsCount);
    }

    @Override
    public AuditEventRecord createAuditEventRecord(AuditEventRecord eventRecord) {

        return auditRecordStorage.addAuditEvent(eventRecord);

    }

}
