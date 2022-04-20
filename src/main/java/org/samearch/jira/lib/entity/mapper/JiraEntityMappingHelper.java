package org.samearch.jira.lib.entity.mapper;

import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.project.Project;

import java.util.Optional;

/**
 * Предоставляет методы, упрощающие доступ к различным сущностям Jira по замапленным ключам.<br/>
 * <br/>
 * Большинство методов возвращают Optional, т.к. информация о наличии значения для указанного ключа имеется только runtime.
 */
public interface JiraEntityMappingHelper extends EntityMapper {

    /**
     * Возвращает проект Jira по идентификатору, сохраненному под указанным ключом.<br/>
     * <br/>
     * Пример использования:<br/>
     * В настройках маппинга создана запись: {@code project.ATLASSIAN_DEVELOPING.id => 12345}<br/>
     * В плагине мы можем использовать следующий код:<pre>
     * {@code
     * final String ATLAS_DEV_PROJECT_ID_KEY = "project.ATLASSIAN_DEVELOPING.id";
     * Optional<Project> atlasDevProjectHolder;
     *
     * atlasDevProjectHolder = jiraEntityMappingHelper.getMappedProjectById(ATLAS_DEV_PROJECT_ID_KEY);
     *
     * if (atlasDevProjectHolder.isPresent()) {
     *     Project atlassianDevelopmentProject = atlasDevProjectHolder.get();
     * }}</pre>
     */
    Optional<Project> getMappedProjectById(String mappedProjectIdKey);

    /**
     * Возвращает объект типа задачи Jira по идентификатору, сохраненному под указанным ключом.<br/>
     * <br/>
     * Пример использования:<br/>
     * В настройках маппинга создана запись: {@code issueType.SUB-TASK.id => 12345}<br/>
     * В плагине мы можем использовать следующий код:<pre>
     * {@code
     * final String ISSUE_TYPE_SUBTASK_ID = "issueType.SUB-TASK.id";
     * Optional<IssueType> subtaskIssueTypeHolder;
     *
     * subtaskIssueTypeHolder = jiraEntityMappingHelper.getMappedIssueTypeById(ISSUE_TYPE_SUBTASK_ID);
     *
     * if (subtaskIssueTypeHolder.isPresent()) {
     *     IssueType subtaskIssueType = atlasDevProjectHolder.get();
     * }}</pre>
     */
    Optional<IssueType> getMappedIssueTypeById(String mappedIssueTypeIdKey);

    /**
     * Возвращает объект пользовательского поля Jira по идентификатору, сохраненному под указанным ключом.<br/>
     * <br/>
     * Пример использования:<br/>
     * В настройках маппинга создана запись: {@code customField.EMPLOYEE.id => customfield_27031}<br/>
     * В плагине мы можем использовать следующий код:<pre>
     * {@code
     * final String CF_EMPLOYEE_ID = "customField.EMPLOYEE.id";
     * Optional<CustomField> cfEmployeeHolder;
     *
     * cfEmployeeHolder = jiraEntityMappingHelper.getMappedCustomFieldById(CF_EMPLOYEE_ID);
     *
     * if (cfEmployeeHolder.isPresent()) {
     *     CustomField cfEmployee = cfEmployeeHolder.get();
     * }}</pre>
     */
    Optional<CustomField> getMappedCustomFieldById(String mappedCustomFieldIdKey);

    /**
     * Возвращает объект статуса задачи Jira по идентификатору, сохраненному под указанным ключом.<br/>
     * <br/>
     * Пример использования:<br/>
     * В настройках маппинга создана запись: {@code issueStatus.REOPENED.id => 12345}<br/>
     * В плагине мы можем использовать следующий код:<pre>
     * {@code
     * final String STATUS_REOPENED_ID = "issueStatus.REOPENED.id";
     * Optional<Status> statusReopenedHolder;
     *
     * statusReopenedHolder = jiraEntityMappingHelper.getMappedIssueStatusById(STATUS_REOPENED_ID);
     *
     * if (statusReopenedHolder.isPresent()) {
     *     Status statusReopened = statusReopenedHolder.get();
     * }}</pre>
     */
    Optional<Status> getMappedIssueStatusById(String mappedIssueStatusIdKey);

}
