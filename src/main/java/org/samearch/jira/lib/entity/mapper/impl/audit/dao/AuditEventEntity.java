package org.samearch.jira.lib.entity.mapper.impl.audit.dao;

import org.samearch.jira.lib.entity.mapper.EntityMappingEvent;
import net.java.ao.OneToMany;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

import java.util.Date;

@Table("AuditEvent")
public interface AuditEventEntity extends RawEntity<Long> {

    @PrimaryKey
    @AutoIncrement
    @NotNull
    Long getId();
    void setId(Long id);

    String getInitiator();
    void setInitiator(String initiator);

    Date getDate();
    void setDate(Date date);

    EntityMappingEvent getEvent();
    void setEvent(EntityMappingEvent event);

    int getMappingId();
    void setMappingId(int mappingId);

    @OneToMany
    AuditEventAdditionalInfoEntity[] getEventAdditionalInfo();

}
