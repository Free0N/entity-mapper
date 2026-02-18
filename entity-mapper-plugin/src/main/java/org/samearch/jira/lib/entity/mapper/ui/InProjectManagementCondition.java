package org.samearch.jira.lib.entity.mapper.ui;

import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import org.samearch.jira.lib.entity.mapper.PluginSettingsManager;
import org.springframework.stereotype.Component;

@Component
public class InProjectManagementCondition extends AbstractWebCondition {

    private final PluginSettingsManager pluginSettingsManager;

    public InProjectManagementCondition(PluginSettingsManager pluginSettingsManager) {
        this.pluginSettingsManager = pluginSettingsManager;
    }

    @Override
    public boolean shouldDisplay(ApplicationUser applicationUser, JiraHelper jiraHelper) {
        return pluginSettingsManager.getMappingSettings().mappingsEnabledInProjects();
    }

}
