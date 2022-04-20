package org.samearch.jira.lib.entity.mapper.impl.mapping.dao;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

@Table("EntityMapping")
public interface EntityMappingEntity extends Entity {

    String getKey();
    void setKey(String key);

    String getValue();
    void setValue(String value);

}
