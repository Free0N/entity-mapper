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

package org.samearch.jira.lib.entity.mapper.ui.rest;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import org.samearch.jira.lib.entity.mapper.ui.RequestUtils;
import org.samearch.jira.lib.entity.mapper.ui.UserPermissionChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Servlet Filter, отвечающий за проверку прав при запросах на REST endpoint-ы entity mapper-а.
 */
@Component
public class PermissionCheckerRestFilter implements Filter {

    private static final Pattern PROJECT_KEY_PATTERN = Pattern.compile("/*rest/entity-mapper/1/project/(?<projectKey>[A-Za-z0-9]+)/mapping.*");

    private final JiraAuthenticationContext jiraAuthenticationContext;

    private final RequestUtils requestUtils;
    private final UserPermissionChecker userPermissionChecker;

    @Autowired
    public PermissionCheckerRestFilter(JiraAuthenticationContext jiraAuthenticationContext,
                                       RequestUtils requestUtils,
                                       UserPermissionChecker userPermissionChecker) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.requestUtils = requestUtils;
        this.userPermissionChecker = userPermissionChecker;
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ApplicationUser currentUser = jiraAuthenticationContext.getLoggedInUser();
        Project requestedProject = requestUtils.extractProjectFromRequestByKey(PROJECT_KEY_PATTERN, (HttpServletRequest) request);
        if (requestedProject != null) {
            if (userPermissionChecker.isUserHasPermissionForMappingManagementInProject(currentUser, requestedProject)) {
                chain.doFilter(request, response);
            } else {
                sendUnauthorized((HttpServletResponse) response);
            }
        } else if (userPermissionChecker.isUserHasPermissionForMappingManagement(currentUser)) {
            chain.doFilter(request, response);
        } else {
            sendUnauthorized((HttpServletResponse) response);
        }
    }

    @Override
    public void destroy() {
    }

    private void sendUnauthorized(HttpServletResponse response) throws IOException {
        response.reset();
        response.setHeader("Content-Type", "application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.flushBuffer();
    }

}
