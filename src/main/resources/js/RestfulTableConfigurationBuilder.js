class RestfulTableConfigurationBuilder {
    buildDefaultConfiguration() {
        return {
            allowReorder: false,
            allowCreate: true,
            allowEdit: true,
            allowDelete: true
        }
    }

    buildRoConfiguration() {
        return {
            allowReorder: false,
            allowCreate: false,
            allowEdit: false,
            allowDelete: false
        }
    }
}
