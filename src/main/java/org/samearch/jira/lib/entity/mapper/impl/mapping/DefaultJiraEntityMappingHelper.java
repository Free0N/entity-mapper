package org.samearch.jira.lib.entity.mapper.impl.mapping;

import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.samearch.jira.lib.entity.mapper.AuditJournal;
import org.samearch.jira.lib.entity.mapper.JiraEntityMappingHelper;
import org.samearch.jira.lib.entity.mapper.exception.ClosedChainEntityMappingException;
import org.samearch.jira.lib.entity.mapper.impl.audit.util.AuditRecordBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@ExportAsService({JiraEntityMappingHelper.class})
public class DefaultJiraEntityMappingHelper extends DefaultEntityMapper implements JiraEntityMappingHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultJiraEntityMappingHelper.class);

    @ComponentImport
    private final ProjectManager projectManager;
    @ComponentImport
    private final IssueTypeManager issueTypeManager;
    @ComponentImport
    private final CustomFieldManager customFieldManager;
    @ComponentImport
    private final StatusManager statusManager;

    @Autowired
    public DefaultJiraEntityMappingHelper(
            ProjectManager projectManager,
            IssueTypeManager issueTypeManager,
            CustomFieldManager customFieldManager,
            StatusManager statusManager,
            EntityMappingManager mappingManager,
            AuditJournal auditJournal,
            AuditRecordBuilder auditRecordBuilder) {

        super(mappingManager, auditJournal, auditRecordBuilder);

        this.projectManager = projectManager;
        this.issueTypeManager = issueTypeManager;
        this.customFieldManager = customFieldManager;
        this.statusManager = statusManager;

    }

    @Override
    public Optional<Project> getMappedProjectById(String mappedProjectIdKey) {

        try {
            return getMappedValue(mappedProjectIdKey).map(projectIdParam -> {
                Optional<Project> mappedProject;
                try {
                    Long projectId = Long.parseLong(projectIdParam);
                    mappedProject = Optional.ofNullable(projectManager.getProjectObj(projectId));
                } catch (NumberFormatException e) {
                    mappedProject = Optional.empty();
                }
                return mappedProject;
            }).orElseGet(Optional::empty);
        } catch (ClosedChainEntityMappingException e) {
            LOG.error("Check mapping configuration", e);
            return Optional.empty();
        }

    }

    @Override
    public Optional<IssueType> getMappedIssueTypeById(String mappedIssueTypeIdKey) {

        try {
            return getMappedValue(mappedIssueTypeIdKey).map(issueTypeManager::getIssueType);
        } catch (ClosedChainEntityMappingException e) {
            LOG.error("Check mapping configuration", e);
            return Optional.empty();
        }

    }

    @Override
    public Optional<CustomField> getMappedCustomFieldById(String mappedCustomFieldIdKey) {

        try {
            return getMappedValue(mappedCustomFieldIdKey).map(customFieldIdParam -> {
                Optional<CustomField> mappedCustomField;
                try {
                    Long customFieldId = Long.parseLong(customFieldIdParam);
                    mappedCustomField = Optional.ofNullable(customFieldManager.getCustomFieldObject(customFieldId));
                } catch (NumberFormatException e) {
                    mappedCustomField = Optional.empty();
                }
                return mappedCustomField;
            }).orElseGet(Optional::empty);
        } catch (ClosedChainEntityMappingException e) {
            LOG.error("Check mapping configuration", e);
            return Optional.empty();
        }

    }

    @Override
    public Optional<Status> getMappedIssueStatusById(String mappedIssueStatusIdKey) {

        try {
            return getMappedValue(mappedIssueStatusIdKey).map(statusManager::getStatus);
        } catch (ClosedChainEntityMappingException e) {
            LOG.error("Check mapping configuration", e);
            return Optional.empty();
        }

    }

}
