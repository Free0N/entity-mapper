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
