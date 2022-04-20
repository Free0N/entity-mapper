package org.samearch.jira.lib.entity.mapper.exception;

public class EntityMappingNotFoundException extends EntityMappingException {

    public EntityMappingNotFoundException(String mappingKey) {
        this.errorMessage = String.format("Mapping with key '%s' not found", mappingKey);
    }

    public EntityMappingNotFoundException(int mappingId) {
        this.errorMessage = String.format("Mapping with #%d not found", mappingId);
    }

}
