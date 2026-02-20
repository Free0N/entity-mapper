import org.samearch.jira.lib.entity.mapper.api.EntityMapper

import com.atlassian.jira.project.Project
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.user.ApplicationUser

@WithPlugin("org.samearch.jira.lib.entity-mapper-plugin")
@PluginModule
EntityMapper entityMapper
@StandardModule
UserManager userManager

Project project = issue.getProjectObject()
if (project == null || project.isArchived()) {
    return
}

String projectKey = project.key
String projectDutyKey = "project.${projectKey}.duty"
entityMapper.getMappedValue(projectDutyKey)
    .ifPresent { dutyUserLogin ->
        ApplicationUser dutyUser = userManager.getUserByName(dutyUserLogin)
        if (dutyUser != null && dutyUser.active) {
            issue.setAssignee(dutyUser)
        }
    }
