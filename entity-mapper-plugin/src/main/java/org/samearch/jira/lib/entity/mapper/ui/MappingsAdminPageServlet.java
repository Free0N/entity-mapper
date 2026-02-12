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

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Сервлет рендерит шаблон, указанный в конфигурации (atlassian-plugin.xml)
 */
@Component
public class MappingsAdminPageServlet extends AbstractMappingsPageServlet {

    @ComponentImport
    private final JiraAuthenticationContext jiraAuthenticationContext;

    private final UserPermissionChecker userPermissionChecker;

    @Autowired
    public MappingsAdminPageServlet(TemplateRenderer templateRenderer,
                                    JiraAuthenticationContext jiraAuthenticationContext,
                                    UserPermissionChecker userPermissionChecker) {
        super(templateRenderer);
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.userPermissionChecker = userPermissionChecker;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ApplicationUser currentUser = jiraAuthenticationContext.getLoggedInUser();
        if (userPermissionChecker.isUserHasPermissionForMappingManagement(currentUser)) {
            super.doGet(request, response);
        } else {
            redirectToLoginPage(request, response);
        }
    }

}
