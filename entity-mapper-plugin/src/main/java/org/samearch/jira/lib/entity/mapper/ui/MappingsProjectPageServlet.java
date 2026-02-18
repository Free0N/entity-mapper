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

package org.samearch.jira.lib.entity.mapper.ui;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.samearch.jira.lib.entity.mapper.PluginSettingsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Сервлет рендерит шаблон, указанный в конфигурации (atlassian-plugin.xml)
 */
@Component
public class MappingsProjectPageServlet extends AbstractMappingsPageServlet {

    /*
     * Паттерн выделения ключа проекта из URL-а вида /entity-mapper/project/${projectKey}/mappings.
     * Подход подсмотрен в реализации плагина jira-project-config-plugin стандартной поставки Jira.
     * Не самый лучший вариант реализации, т.к. в строке паттерна фигурирует URL, который может меняться.
     */
    private static final Pattern PROJECT_KEY_PATTERN = Pattern.compile("/*entity-mapper/project/(?<projectKey>[A-Za-z0-9]+)/mappings");

    @ComponentImport
    private final JiraAuthenticationContext jiraAuthenticationContext;

    private final RequestUtils requestUtils;
    private final UserPermissionChecker userPermissionChecker;
    private final PluginSettingsManager pluginSettingsManager;

    @Autowired
    public MappingsProjectPageServlet(TemplateRenderer templateRenderer,
                                      JiraAuthenticationContext jiraAuthenticationContext,
                                      RequestUtils requestUtils,
                                      UserPermissionChecker userPermissionChecker,
                                      PluginSettingsManager pluginSettingsManager) {
        super(templateRenderer);
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.requestUtils = requestUtils;
        this.userPermissionChecker = userPermissionChecker;
        this.pluginSettingsManager = pluginSettingsManager;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ApplicationUser currentUser = jiraAuthenticationContext.getLoggedInUser();
        Project currentProject = requestUtils.extractProjectFromRequestByKey(PROJECT_KEY_PATTERN, request);
        if (currentProject != null && userPermissionChecker.isUserHasPermissionForMappingManagementInProject(currentUser, currentProject)) {
            super.doGet(request, response);
        } else {
            redirectToLoginPage(request, response);
        }
    }

    @Override
    protected Map<String, Object> prepareTemplateRenderingContext(HttpServletRequest request) {
        Map<String, Object> mainRenderingContext = super.prepareTemplateRenderingContext(request);
        mainRenderingContext.put("project", requestUtils.extractProjectFromRequestByKey(PROJECT_KEY_PATTERN, request));
        mainRenderingContext.put("pluginSettings", pluginSettingsManager.getMappingSettings());
        return mainRenderingContext;
    }

}
