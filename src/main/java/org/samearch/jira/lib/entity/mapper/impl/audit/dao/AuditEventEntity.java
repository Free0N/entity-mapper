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

package org.samearch.jira.lib.entity.mapper.impl.audit.dao;

import net.java.ao.OneToMany;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;
import org.samearch.jira.lib.entity.mapper.EntityMappingEvent;

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
