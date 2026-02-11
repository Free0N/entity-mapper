package org.samearch.jira.lib.entity.mapper.ui;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RequestUtils {

    @ComponentImport
    private final ProjectManager projectManager;

    @Autowired
    public RequestUtils(ProjectManager projectManager) {
        this.projectManager = projectManager;
    }

    /**
     * Выделяет из URL-а запроса ключ проекта и возвращает объект этого проекта.
     * В паттерне должна быть описана группа projectKey
     * ВНИМАНИЕ! Для определения выбирается только servletPath без дополнительных полей!
     */
    public Project extractProjectFromRequestByKey(Pattern pattern, HttpServletRequest request) {
        String projectKey = tryToExtractProjectKey(request.getPathInfo(), pattern);
        if (projectKey == null) {
            projectKey = tryToExtractProjectKey(request.getServletPath(), pattern);
        }
        if (projectKey == null) {
            return null;
        }
        return projectManager.getProjectByCurrentKey(projectKey);
    }

    private String tryToExtractProjectKey(String urlPart, Pattern pattern) {
        if (urlPart == null || urlPart.trim().isEmpty()) {
            return null;
        }
        Matcher matcher = pattern.matcher(urlPart);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group("projectKey");
    }

}
