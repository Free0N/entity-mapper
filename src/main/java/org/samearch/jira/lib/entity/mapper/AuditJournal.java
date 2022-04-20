package org.samearch.jira.lib.entity.mapper;

import java.util.List;

public interface AuditJournal {

    /**
     * Возвращает список последних событий аудита.
     *
     * @param lastEventsCount Количество возвращаемых записей о событиях. Если указанное число больше, чем существует
     *                        записей, то сервис вернет все существующие записи.
     */
    List<AuditEventRecord> getLastEvents(int lastEventsCount);

    /**
     * Сохраняет в журнал запись о событии, произошедшем с паммингом.<br/>
     * <br/>
     * В передаваемой записи поле {@code id} ингнорируется.
     */
    AuditEventRecord createAuditEventRecord(AuditEventRecord eventRecord);

}
