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

package org.samearch.jira.lib.entity.mapper.api;

import java.util.List;

/**
 * Интерфейс к журналу аудита действий над маппингами.
 */
public interface AuditJournal {

    /**
     * Возвращает список последних событий аудита.
     *
     * @param eventsFilter фильтр, применяемый для выборки информации о событиях
     * @return список событий журнала аудита, которые соответствуют указанному фильтру
     */
    List<AuditEventRecord> getEvents(AuditJournalFilter eventsFilter);

    /**
     * Сохраняет в журнал запись о событии, произошедшем с паммингом.<br>
     * <br>
     * В передаваемой записи поле {@code id} ингнорируется.
     *
     * @param eventRecord заполненный объект записи журнала аудита. В этом объекте поле id заполнять не требуется
     * @return созданный объект записи журнала аудита. В этом объекте заполнено поле id
     */
    AuditEventRecord createAuditEventRecord(AuditEventRecord eventRecord);

}
