package org.samearch.jira.lib.entity.mapper;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.samearch.jira.lib.entity.mapper.ui.rest.dto.MappingSettingsDto;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlElement;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PluginSettingsManager {

    private final PluginSettings pluginSettings;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PluginSettingsManager(@ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettings = pluginSettingsFactory.createSettingsForKey("entity-mapper.settings");
    }

    public MappingSettingsDto getMappingSettings() {
        MappingSettingsDto mappingSettings = new MappingSettingsDto();
        for (Field settingsField : MappingSettingsDto.class.getDeclaredFields()) {
            readSettingForField(mappingSettings, settingsField);
        }
        return mappingSettings;
    }

    private void readSettingForField(MappingSettingsDto mappingSettings, Field field) {
        String fieldName = getFieldXmlName(field);
        if (fieldName == null) {
            return;
        }
        Object savedFieldValue = pluginSettings.get(fieldName);
        if (savedFieldValue == null) {
            return;
        }
        Object actualFieldValue = objectMapper.convertValue(savedFieldValue.toString().toLowerCase(), field.getType());
        try {
            field.setAccessible(true);
            field.set(mappingSettings, actualFieldValue);
            field.setAccessible(false);
        } catch (Exception ignore) {}
    }

    private String getFieldXmlName(Field field) {
        XmlElement elementFieldAnnotation = field.getAnnotation(XmlElement.class);
        if (elementFieldAnnotation == null) {
            return null;
        }
        return elementFieldAnnotation.name();
    }

    public void updatePluginSettings(MappingSettingsDto settingsDto) {
        getPluginSettingKeys().forEach(pluginSettingKey -> {
            try {
                Field pluginSetting = MappingSettingsDto.class.getDeclaredField(pluginSettingKey);
                pluginSetting.setAccessible(true);
                Object currentSettingValue = pluginSetting.get(settingsDto);
                pluginSetting.setAccessible(false);
                String settingValueRepresentation = objectMapper.writeValueAsString(currentSettingValue);
                pluginSettings.put(pluginSettingKey, settingValueRepresentation);
            } catch (Exception ignore) {}
        });
    }

    private Set<String> getPluginSettingKeys() {
        return Arrays.stream(MappingSettingsDto.class.getDeclaredFields())
                .map(this::getFieldXmlName)
                .collect(Collectors.toSet());
    }

}
