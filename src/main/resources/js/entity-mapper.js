AJS.toInit((jQuery) => {
    let tableConfigurationBuilder = new RestfulTableConfigurationBuilder();

    // Создание элемента основной таблицы настройки маппингов
    let entityMappingsTableElement = jQuery("#entity_mappings");
    if (entityMappingsTableElement.length === 0) {
        return;
    }

    let mappingsListEndpoint = AJS.contextPath() + "/rest/entity-mapper/1/settings/mappings";
    let mappingCrudEndpoint = AJS.contextPath() + "/rest/entity-mapper/1/settings/mapping";

    let mainEntityMappingTableConfiguration = tableConfigurationBuilder.buildDefaultConfiguration();
    mainEntityMappingTableConfiguration.el = entityMappingsTableElement;
    mainEntityMappingTableConfiguration.resources = {
        all: mappingsListEndpoint,
        self: mappingCrudEndpoint
    }
    mainEntityMappingTableConfiguration.columns = [{
        id: "key",
        header: "Mapping key"
    }, {
        id: "value",
        header: "Value"
    }];

    window.entityMappingMainTable = new AJS.RestfulTable(mainEntityMappingTableConfiguration);

    AJS.$("#show-last-audit-records-button").click(function(e) {
        e.preventDefault();
        let entityMappingAuditLogTable = window.entityMappingAuditLogTable;
        entityMappingAuditLogTable.getRows()
            .forEach((row) => {
                entityMappingAuditLogTable.removeRow(row);
            });
        entityMappingAuditLogTable.fetchInitialResources();
        window.auditDialogWindow.show();
    });

    jQuery(document).ajaxError((event, jqxhr) => {
        var unknownErrorMessage = 'Неизвестная ошибка. Обратитесь к администратору.';
        var errorMessage = (JSON.parse(jqxhr.responseText) || {errorMessage: ''}).errorMessage || unknownErrorMessage;

        require('aui/flag')({
            type: 'error',
            title: 'Ошибка при выполнении операции',
            body: errorMessage
        });
    });
});
