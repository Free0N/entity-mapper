package org.samearch.jira.lib.entity.mapper.ui.rest.dto;

import org.samearch.jira.lib.entity.mapper.EntityMappingEvent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;
import java.util.Objects;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AuditEventRecordDto {

    private long id;
    private String date;
    private String initiator;
    private EntityMappingEvent event;
    private int mappingId;
    private Map<String, String> additionalInformation;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public EntityMappingEvent getEvent() {
        return event;
    }

    public void setEvent(EntityMappingEvent event) {
        this.event = event;
    }

    public int getMappingId() {
        return mappingId;
    }

    public void setMappingId(int mappingId) {
        this.mappingId = mappingId;
    }

    public Map<String, String> getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(Map<String, String> additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuditEventRecordDto that = (AuditEventRecordDto) o;

        if (id != that.id) return false;
        if (mappingId != that.mappingId) return false;
        if (!Objects.equals(date, that.date)) return false;
        if (!Objects.equals(initiator, that.initiator)) return false;
        if (event != that.event) return false;
        return Objects.equals(additionalInformation, that.additionalInformation);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (initiator != null ? initiator.hashCode() : 0);
        result = 31 * result + (event != null ? event.hashCode() : 0);
        result = 31 * result + mappingId;
        result = 31 * result + (additionalInformation != null ? additionalInformation.hashCode() : 0);
        return result;
    }

}
