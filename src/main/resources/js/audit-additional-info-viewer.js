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
