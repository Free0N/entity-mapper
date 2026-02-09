var AuditJournal = Backbone.Collection.extend({
    url: AJS.contextPath() + "/rest/entity-mapper/1/audit/records"
});

var AuditJournalView = Backbone.View.extend({

    initialize: function(args) {
        this.listenTo(this.model, "sync", this.render)
    },

    render: function() {
        var logMessages = [];
        this.model.models.forEach((auditRecord) => {
            logMessages.push(this._buildAuditRecordUiRow(auditRecord));
        });
        if (logMessages.length > 0) {
            $(`#${this.id}`).html(`<code>${logMessages.join("<br>\n")}</code>`);
        } else {
            $(`#${this.id}`).html("<code>No audit records found</code>");
        }
        return this;
    },

    _buildAuditRecordUiRow: function(auditRecord) {
        let date = auditRecord.get("date");
        let user = auditRecord.get("initiator");
        let event = auditRecord.get("event");
        let mappingId = auditRecord.get("mappingId");
        let description = this._buildAuditRecordDescription(auditRecord);
        return `[${date}] ${user} ${event} #${mappingId}: ${description}`;
    },

    _buildAuditRecordDescription: function(auditRecord) {
        let eventInfo = auditRecord.get("additionalInformation");
        switch (auditRecord.get("event")) {
            case "CREATE": {
                let k = eventInfo.key;
                let v = eventInfo.value;
                return `${k}: ${v}`;
            }
            case "UPDATE": {
                let ok = eventInfo["oldMapping.key"];
                let ov = eventInfo["oldMapping.value"];
                let nk = eventInfo["newMapping.key"];
                let nv = eventInfo["newMapping.value"];
                let messages = [];
                if (ok !== nk) {
                    messages.push(`key: ${ok} => ${nk}`);
                }
                if (ov !== nv) {
                    messages.push(`value: ${ov} => ${nv}`);
                }
                return messages.length !== 0
                    ? messages.join("; ")
                    : "no updates found"
            }
            case "DELETE": {
                let k = eventInfo.key;
                let v = eventInfo.value;
                return `${k}: ${v}`;
            }
            default:
                return "Unknonwn update option";
        }
    }

});

AJS.EntityMapper = AJS.EntityMapper || {};
AJS.EntityMapper.AuditJournal = AuditJournal;
AJS.EntityMapper.AuditJournalView = AuditJournalView;