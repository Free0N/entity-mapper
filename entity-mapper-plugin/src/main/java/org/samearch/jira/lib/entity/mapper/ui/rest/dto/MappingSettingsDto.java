package org.samearch.jira.lib.entity.mapper.ui.rest.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Настройки плагина.
 * Поля, указанные в этом объекте обрабатываются {@link org.samearch.jira.lib.entity.mapper.PluginSettingsManager}
 * Каждое поле при сохранении настроек записывается в {@link com.atlassian.sal.api.pluginsettings.PluginSettings}
 * в текстовом виде. Поле запишется только в том случае, если имеет аннотацию {@link XmlElement} с указанным атрибутом
 * {@code name} и если сможет быть сериализовано в строку с помощью ObjectMapper-а из Jackson.
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class MappingSettingsDto {

    @XmlElement(name = "mappingsEnabledInProjects")
    private Boolean mappingsEnabledInProjects = Boolean.FALSE;

    public Boolean mappingsEnabledInProjects() {
        return mappingsEnabledInProjects;
    }

    public void setMappingsEnabledInProjects(Boolean mappingsEnabledInProjects) {
        this.mappingsEnabledInProjects = mappingsEnabledInProjects;
    }

}
