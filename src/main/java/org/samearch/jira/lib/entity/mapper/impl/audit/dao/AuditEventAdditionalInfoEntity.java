package org.samearch.jira.lib.entity.mapper.impl.audit.dao;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

@Table("AuditEventInfo")
public interface AuditEventAdditionalInfoEntity extends Entity {

    String getKey();
    void setKey(String key);

    String getValue();
    void setValue(String value);

    AuditEventEntity getAuditEventEntity();
    void setAuditEventEntity(AuditEventEntity eventEntity);

}
