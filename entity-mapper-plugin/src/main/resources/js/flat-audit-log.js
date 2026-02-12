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

AJS.toInit(() => {
    var dateFilterObjects = initDateFilterElements();
    var auditJournalObjects = initAuditJournal({
        ...dateFilterObjects,
        journalElementId: "audit-journal"
    });
    initRefreshButton({
        ...auditJournalObjects,
        initiatorFilterFieldId: "journal-filter-initiator",
        eventFilterFieldId: "journal-filter-event",
        mappingIdFilterId: "journal-filter-mappingId",
        refreshButtonId: "journal-filter-refresh-button"
    });
});

function initDateFilterElements() {
    const datePickerOptions = {
        dateFormat: "dd-mm-yy",
        overrideBrowserDefault: true
    };
    const startDatePickerElement = document.getElementById("journal-filter-start-date-picker");
    const startDatePicker = new AJS.DatePicker(startDatePickerElement, { ...datePickerOptions, placeholder: "Start Date" });
    const endDatePickerElement = document.getElementById("journal-filter-end-date-picker");
    const endDatePicker = new AJS.DatePicker(endDatePickerElement, { ...datePickerOptions, placeholder: "End Date" });

    return {
        startDatePicker: startDatePicker,
        endDatePicker: endDatePicker
    };
}

function initAuditJournal(args) {
    var auditJournal = new AJS.EntityMapper.AuditJournal();
    var auditJournalView = new AJS.EntityMapper.AuditJournalView({model: auditJournal, id: args.journalElementId});
    return {
        ...args,
        auditJournal: auditJournal,
        auditJournalView: auditJournalView
    };
}

function initRefreshButton(args) {
    AJS.$(document).on("click", `#${args.refreshButtonId}`, function(e) {
        e.preventDefault();
        var startDate = args.startDatePicker.getDate();
        var formattedStartDate = AJS.$.datepicker.formatDate("yymmdd", startDate);
        var endDate = args.endDatePicker.getDate();
        var formattedEndDate = AJS.$.datepicker.formatDate("yymmdd", endDate);
        var initiator = $(`#${args.initiatorFilterFieldId}`).val();
        var event = $(`#${args.eventFilterFieldId}`).val();
        var mappingId = $(`#${args.mappingIdFilterId}`).val();

        var requestFilter = {};
        if (args.startDatePicker.getField().val()) {
            requestFilter["startDate"] = formattedStartDate;
        }
        if (args.endDatePicker.getField().val()) {
            requestFilter["endDate"] = formattedEndDate;
        }
        if (initiator) {
            requestFilter["initiator"] = initiator;
        }
        if (event) {
            requestFilter["event"] = event;
        }
        if (mappingId) {
            requestFilter["mappingId"] = mappingId;
        }

        args.auditJournal.fetch({ reset: true, data: requestFilter});
    });
}
