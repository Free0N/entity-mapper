AJS.EntityMapper = AJS.EntityMapper || {};
AJS.EntityMapper.restEndpoint = function (pluginPath) {
    return `${AJS.contextPath()}/rest/entity-mapper/1${pluginPath}`;
}