package org.samearch.jira.lib.entity.mapper.auditeventinfo;

import org.samearch.jira.lib.entity.mapper.AuditJournal;
import org.samearch.jira.lib.entity.mapper.EntityMapping;

import java.util.Objects;

/**
 * POJO объединяет представления для двух разных версий маппинга.<br/>
 * Предполагается использование только сервисом журналирования
 * ({@link AuditJournal AuditJournalService}).
 */
public class EntityMappingUpdateInfo {

    private EntityMapping oldValue;
    private EntityMapping newValue;

    public EntityMapping getOldValue() {
        return oldValue;
    }

    public void setOldValue(EntityMapping oldValue) {
        this.oldValue = oldValue;
    }

    public EntityMapping getNewValue() {
        return newValue;
    }

    public void setNewValue(EntityMapping newValue) {
        this.newValue = newValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityMappingUpdateInfo that = (EntityMappingUpdateInfo) o;

        if (!Objects.equals(oldValue, that.oldValue)) return false;
        return Objects.equals(newValue, that.newValue);
    }

    @Override
    public int hashCode() {
        int result = oldValue != null ? oldValue.hashCode() : 0;
        result = 31 * result + (newValue != null ? newValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EntityMappingUpdateInfo{" +
                "oldValue=" + oldValue +
                ", newValue=" + newValue +
                '}';
    }

}
