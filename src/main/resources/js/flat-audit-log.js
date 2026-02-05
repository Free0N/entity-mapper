AJS.toInit(() => {
    var auditJournal = new AJS.EntityMapper.AuditJournal();
    var auditJournalView = new AJS.EntityMapper.AuditJournalView({model: auditJournal, id: "audit-journal"});
    auditJournal.fetch({reset: true, data: {startDate: "20260203"}});
});
