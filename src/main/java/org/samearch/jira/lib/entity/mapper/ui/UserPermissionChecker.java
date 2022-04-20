package org.samearch.jira.lib.entity.mapper.ui;

import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserPermissionChecker {

    @ComponentImport
    private final GlobalPermissionManager globalPermissionManager;

    @Autowired
    public UserPermissionChecker(GlobalPermissionManager globalPermissionManager) {
        this.globalPermissionManager = globalPermissionManager;
    }

    public boolean isUserHasPermissionForMappingManagement(ApplicationUser user) {
        return globalPermissionManager.hasPermission(GlobalPermissionKey.SYSTEM_ADMIN, user);
    }

}
