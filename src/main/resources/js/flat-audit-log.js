AJS.toInit(() => {
    initFiltersBar();
    initAuditJournal("audit-journal");
});

function initFiltersBar() {
    const datePickerOptions = {
        dateFormat: "dd-mm-yy",
        overrideBrowserDefault: true
    };
    const startDatePickerElement = document.getElementById("journal-filter-start-date-picker");
    const startDatePicker = new AJS.DatePicker(startDatePickerElement, { ...datePickerOptions, placeholder: "Start Date" });
    const endDatePickerElement = document.getElementById("journal-filter-end-date-picker");
    const endDatePicker = new AJS.DatePicker(endDatePickerElement, { ...datePickerOptions, placeholder: "End Date" });

    AJS.$(document).on("click", "#journal-filter-refresh-button", function(e) {
        e.preventDefault();
        console.log("try to refresh journal log");
    });
}

function initAuditJournal(elementId) {
    var auditJournal = new AJS.EntityMapper.AuditJournal();
    var auditJournalView = new AJS.EntityMapper.AuditJournalView({model: auditJournal, id: elementId});
    auditJournal.fetch({reset: true, data: {startDate: "20260203"}});
}