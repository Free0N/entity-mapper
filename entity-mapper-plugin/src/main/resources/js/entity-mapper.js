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

AJS.toInit((jQuery) => {
    initMainTable(jQuery);
    initPerProjectManagementToggle(jQuery);
    initAjaxErrorHandler(jQuery);
});

function initMainTable(jQuery) {
    let tableConfigurationBuilder = new AJS.EntityMapper.RestfulTableConfigurationBuilder();

    // Создание элемента основной таблицы настройки маппингов
    let entityMappingsTableElement = jQuery("#entity_mappings");
    if (entityMappingsTableElement.length === 0) {
        return;
    }

    let mappingsListEndpoint = AJS.EntityMapper.restEndpoint("/mapping");
    let mappingCrudEndpoint = AJS.EntityMapper.restEndpoint("/mapping");

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
}

function initPerProjectManagementToggle(jQuery) {
    jQuery(document).on("click", `#mapping-per-project-switcher`, function (e) {
        e.preventDefault();
        var checker = document.getElementById("mapping-per-project-switcher");
        checker.busy = true;
        var requestData = {
            mappingsEnabledInProjects: checker.checked
        };
        jQuery.ajax({
            url: AJS.EntityMapper.restEndpoint("/settings"),
            type: "PUT",
            data: JSON.stringify(requestData),
            contentType: "application/json",
            success: function (response) {
                checker.busy = false;
            },
            error: function (xhr, status, error) {
                checker.checked = !requestData.mappingsEnabledInProjects;
                checker.busy = false;
            }
        });
    });
}

function initAjaxErrorHandler(jQuery) {
    jQuery(document).ajaxError((event, jqxhr) => {
        var unknownErrorMessage = "Неизвестная ошибка. Обратитесь к администратору.";
        var errorMessage = (JSON.parse(jqxhr.responseText) || {errorMessage: ''}).errorMessage || unknownErrorMessage;

        require('aui/flag')({
            type: "error",
            title: "Ошибка при выполнении операции",
            body: errorMessage
        });
    });
}
