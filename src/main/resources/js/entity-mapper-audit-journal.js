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

    /*
     * Создание элемента для отображения записей аудита.
     * Этот элемент создается один раз и переиспользуется для отображения различных журналов аудита. Если пользователь
     * нажимает на кнопку в верхней части формы управления маппингами, то отображается список записей аудита без фильтра
     * по конкретному элементу.
     * Если же пользователь нажимает на ссылку "Audit" рядом с элементом маппинга, то эта таблица отображает аудит по
     * конкретному элементу маппинга.
     * Такое поведение достигается за счет изменения парамтра resources.all и рефреша таблицы.
     */
    let auditRecordsTableElement = jQuery("#audit_log");
    if (auditRecordsTableElement.length !== 0) {
        let auditRecordsListEndpoint = AJS.contextPath() + "/rest/entity-mapper/1/audit/records";

        let auditLogTableConfiguration = new AJS.EntityMapper.RestfulTableConfigurationBuilder().buildRoConfiguration();
        auditLogTableConfiguration.el = auditRecordsTableElement;
        auditLogTableConfiguration.resources = {
            all: auditRecordsListEndpoint
        };
        auditLogTableConfiguration.columns = [{
            id: "date",
            header: "Date"
        }, {
            id: "initiator",
            header: "User",
        }, {
            id: "event",
            header: "Action"
        }, {
            id: "mappingId",
            header: "Mapping ID"
        }, {
            id: "additionalInformation",
            header: "Description",
            readView: AJS.EntityMapper.AuditAdditionalInfoViewer
        }];

        window.entityMappingAuditLogTable = new AJS.RestfulTable(auditLogTableConfiguration);
    }

});
