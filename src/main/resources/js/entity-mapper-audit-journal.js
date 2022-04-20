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

        let auditLogTableConfiguration = new RestfulTableConfigurationBuilder().buildRoConfiguration();
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
            readView: AuditAdditionalInfoViewer
        }];

        window.entityMappingAuditLogTable = new AJS.RestfulTable(auditLogTableConfiguration);
    }

});
