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

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import org.samearch.jira.lib.entity.mapper.ui.UserPermissionChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet Filter, отвечающий за проверку прав при запросах на REST endpoint-ы entity mapper-а.
 */
@Component
public class PermissionCheckerRestFilter implements Filter {

    private final JiraAuthenticationContext jiraAuthenticationContext;

    private final UserPermissionChecker userPermissionChecker;

    @Autowired
    public PermissionCheckerRestFilter(JiraAuthenticationContext jiraAuthenticationContext, UserPermissionChecker userPermissionChecker) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.userPermissionChecker = userPermissionChecker;
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        ApplicationUser currentUser = jiraAuthenticationContext.getLoggedInUser();

        if (!userPermissionChecker.isUserHasPermissionForMappingManagement(currentUser)) {
            response.reset();
            ((HttpServletResponse) response).setHeader("Content-Type", "application/json");
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.flushBuffer();
            return;
        }

        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {

    }

}
