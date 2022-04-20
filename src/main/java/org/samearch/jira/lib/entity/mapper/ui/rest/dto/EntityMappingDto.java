package org.samearch.jira.lib.entity.mapper.ui.rest.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Описывает транспортный объект для записи маппинга.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityMappingDto {

    /** Уникальный идентификатор маппинга */
    private int id;
    /** Ключ маппинга */
    private String key;
    /** Замапленное значение */
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
