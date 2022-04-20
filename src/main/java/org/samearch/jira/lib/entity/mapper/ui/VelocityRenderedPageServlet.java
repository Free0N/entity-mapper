package org.samearch.jira.lib.entity.mapper.ui;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервлет рендерит шаблон, указанный в конфигурации (atlassian-plugin.xml)
 */
@Component
public class VelocityRenderedPageServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(VelocityRenderedPageServlet.class);

    private static final String TEMPLATE_PATH_PARAM_NAME = "template";

    @ComponentImport
    private final TemplateRenderer templateRenderer;
    @ComponentImport
    private final JiraAuthenticationContext jiraAuthenticationContext;

    private final UserPermissionChecker userPermissionChecker;

    @Autowired
    public VelocityRenderedPageServlet(TemplateRenderer templateRenderer,
                                       JiraAuthenticationContext jiraAuthenticationContext,
                                       UserPermissionChecker userPermissionChecker) {

        this.templateRenderer = templateRenderer;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.userPermissionChecker = userPermissionChecker;

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ApplicationUser currentUser = jiraAuthenticationContext.getLoggedInUser();

        if (!userPermissionChecker.isUserHasPermissionForMappingManagement(currentUser)) {
            redirectToLoginPage(request, response);
            return;
        }

        String templatePath = getInitParameter(TEMPLATE_PATH_PARAM_NAME);
        Map<String, Object> templateRenderingContext = prepareTemplateRenderingContext(request);

        try {
            templateRenderer.render(templatePath, templateRenderingContext, response.getWriter());
        } catch (IOException e) {
            LOG.error("Can't render template: {}", templatePath, e);
        }

    }

    private void redirectToLoginPage(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String queryString = request.getQueryString();

        String returnUrl = queryString != null
                ? String.format("%s?%s", servletPath, queryString)
                : servletPath;

        String loginPageUrl = contextPath + "/login.jsp?os_destination=" + returnUrl;

        response.sendRedirect(loginPageUrl);

    }

    private Map<String, Object> prepareTemplateRenderingContext(HttpServletRequest request) {

        Map<String, Object> renderingContext = new HashMap<>();

        renderingContext.put("contextPath", request.getContextPath());

        return renderingContext;

    }

}
