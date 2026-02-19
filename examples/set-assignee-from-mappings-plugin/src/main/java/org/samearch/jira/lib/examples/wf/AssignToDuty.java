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

package org.samearch.jira.lib.examples.wf;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.opensymphony.module.propertyset.PropertySet;
import org.samearch.jira.lib.entity.mapper.api.EntityMapper;
import org.samearch.jira.lib.entity.mapper.api.exception.ClosedChainEntityMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Постфункция устанавливает исполнителья задачи в зависимости от настроенных маппингов.<br>
 * <br>
 * Если создать маппинг следующего вида:
 * <pre>
 * {@code
 * project.AD.duty = admin
 * }
 * </pre>
 * то для задач проекта с ключем AD исполнителем будет назначаться пользователь с логином {@code admin}.<br>
 * <br>
 * Так же можно создать следующие маппинги:
 * <pre>
 * {@code
 * org.CEO => ivan.ivanov
 * developmentDepartment.manager => ${org.CEO}
 * service.jira.duty => ${developmentDepartment.manager}
 * project.AD.duty => ${service.jira.duty}
 * }
 * </pre>
 * то для задач проекта с ключем AD исполнителем будет назначаться пользователь с логином {@code ivan.ivanov}.
 */
@Component
public class AssignToDuty extends AbstractJiraFunctionProvider {

    private static final String PROJECT_DUTY_KEY_TEMPLATE = "project.%s.duty";

    @ComponentImport
    private final EntityMapper entityMapper;
    @ComponentImport
    private final UserManager userManager;

    @Autowired
    public AssignToDuty(EntityMapper entityMapper, UserManager userManager) {
        this.entityMapper = entityMapper;
        this.userManager = userManager;
    }

    @Override
    public void execute(Map transientVars, Map args, PropertySet ps) {
        MutableIssue issue = getIssue(transientVars);
        Project project = issue.getProjectObject();
        if (project == null || project.isArchived()) {
            return;
        }
        String projectKey = project.getKey();
        String projectDutyKey = String.format(PROJECT_DUTY_KEY_TEMPLATE, projectKey);
        try {
            entityMapper.getMappedValue(projectDutyKey)
                    .ifPresent(dutyLogin -> setIssueAssignee(issue, dutyLogin));
        } catch (ClosedChainEntityMappingException ignore) {
            // Исключение игнорируется, т.к. будем аккуратно пользоваться ссылками в маппингах и не будем
            // создавать циклических ссылок
        }
    }

    private void setIssueAssignee(MutableIssue issue, String assigneeLogin) {
        if (issue.getAssignee() != null) {
            return;
        }
        ApplicationUser user = userManager.getUserByName(assigneeLogin);
        if (user != null && user.isActive()) {
            issue.setAssignee(user);
        }
    }
}