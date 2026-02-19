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
import org.samearch.jira.lib.entity.mapper.api.EntityMapper;
import org.samearch.jira.lib.entity.mapper.api.JiraEntityMappingHelper;
import org.samearch.jira.lib.entity.mapper.api.exception.ClosedChainEntityMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@ExportAsService({JiraEntityMappingHelper.class})
public class DefaultJiraEntityMappingHelper implements JiraEntityMappingHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultJiraEntityMappingHelper.class);

    private final EntityMapper entityMapper;

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
            EntityMapper entityMapper) {

        this.projectManager = projectManager;
        this.issueTypeManager = issueTypeManager;
        this.customFieldManager = customFieldManager;
        this.statusManager = statusManager;

        this.entityMapper = entityMapper;
    }

    @Override
    public Optional<Project> getMappedProjectById(String mappedProjectIdKey) {

        try {
            return entityMapper.getMappedValue(mappedProjectIdKey).map(projectIdParam -> {
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
            return entityMapper.getMappedValue(mappedIssueTypeIdKey).map(issueTypeManager::getIssueType);
        } catch (ClosedChainEntityMappingException e) {
            LOG.error("Check mapping configuration", e);
            return Optional.empty();
        }

    }

    @Override
    public Optional<CustomField> getMappedCustomFieldById(String mappedCustomFieldIdKey) {
        try {
            return entityMapper.getMappedValue(mappedCustomFieldIdKey)
                    .flatMap(this::getCfById);
        } catch (ClosedChainEntityMappingException e) {
            LOG.error("Check mapping configuration", e);
            return Optional.empty();
        }
    }

    private Optional<CustomField> getCfById(String cfIdString) {
        try {
            Long cfId = Long.parseLong(cfIdString);
            return Optional.ofNullable(customFieldManager.getCustomFieldObject(cfId));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Status> getMappedIssueStatusById(String mappedIssueStatusIdKey) {
        try {
            return entityMapper.getMappedValue(mappedIssueStatusIdKey).map(statusManager::getStatus);
        } catch (ClosedChainEntityMappingException e) {
            LOG.error("Check mapping configuration", e);
            return Optional.empty();
        }
    }

}
