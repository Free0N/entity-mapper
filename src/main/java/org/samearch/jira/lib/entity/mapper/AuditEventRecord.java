package org.samearch.jira.lib.entity.mapper;

import com.atlassian.jira.user.ApplicationUser;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Запись журнала аудита.<br/>
 * <br/>
 * Предоставляет информацию об одном действии, совершенном пользователем с какой-либо записью маппинга.<br/>
 * Поле {@code additionalInformation} зависит от типа действия.
 */
public class AuditEventRecord {

    /** Идентификатор записи журнала событий. Каждая запись журнала имеет уникальный идентификатор. */
    private long id;

    /** Пользователь-инициатор события. Чаще всего - администратор Jira. */
    private ApplicationUser initiator;

    /** Момент времени, в который произошло событие. */
    private ZonedDateTime date;

    /** Тип события. */
    private EntityMappingEvent event;

    /** Идентификатор записи маппинга, с которой произошло событие. */
    private int mappingId;

    /**
     * Дополнительная информация о событии.<br/>
     * <br/>
     * Содержимое этого поля зависит от типа события.<br/>
     * Например, для события создания записи в этом поле может сохраняться информация о начальных значениях ключа и
     * значения записи. Для события изменения записи здесь может сохраняться информация о поле, которое изменилось - ключ
     * или значение маппинга, о его старом и новом значениях.
     */
    private Map<String, String> additionalInformation;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ApplicationUser getInitiator() {
        return initiator;
    }

    public void setInitiator(ApplicationUser initiator) {
        this.initiator = initiator;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
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

    public void setAdditionalInformation(Map<String,String> additionalInformation) {
        this.additionalInformation = new HashMap<>(additionalInformation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuditEventRecord that = (AuditEventRecord) o;

        if (id != that.id) return false;
        if (mappingId != that.mappingId) return false;
        if (!Objects.equals(initiator, that.initiator)) return false;
        if (!Objects.equals(date, that.date)) return false;
        if (event != that.event) return false;
        return Objects.equals(additionalInformation, that.additionalInformation);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (initiator != null ? initiator.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (event != null ? event.hashCode() : 0);
        result = 31 * result + mappingId;
        result = 31 * result + (additionalInformation != null ? additionalInformation.hashCode() : 0);
        return result;
    }

}
