let AuditAdditionalInfoViewer = AJS.RestfulTable.CustomReadView.extend({
    render: function (additionalInformationCell) {
        let modelAttributes = this.model.attributes;
        let eventType = modelAttributes.event;
        let cellValue = additionalInformationCell.value;

        let html = "<p>" + JSON.stringify(cellValue) + "</p>";

        if (eventType === "CREATE") {
            html = "<p>Mapping created<br><ul><li>" + cellValue.key + " = " + cellValue.value + "</li></ul></p>";
        } else if (eventType === "UPDATE") {
            let oldMapping = {
                key: cellValue["oldMapping.key"],
                value: cellValue["oldMapping.value"]
            }

            let newMapping = {
                key: cellValue["newMapping.key"],
                value: cellValue["newMapping.value"]
            }

            let mappingUpdatedHtml = "<p>Mapping updated<br>";

            if (oldMapping.key !== newMapping.key) {
                mappingUpdatedHtml += "Key: <b>" + oldMapping.key + "</b> => <b>" + newMapping.key + "</b><br>"
            }

            if (oldMapping.value !== newMapping.value) {
                mappingUpdatedHtml += "Value: <b>" + oldMapping.value + "</b> => <b>" + newMapping.value + "</b><br>";
            }

            mappingUpdatedHtml += "</p>";

            html = mappingUpdatedHtml;
        } else if (eventType === "DELETE") {
            html = "<p>Mapping removed<br><ul><li>" + cellValue.key + " = " + cellValue.value + "</li></ul></p>";
        }

        return html;
    }
});
