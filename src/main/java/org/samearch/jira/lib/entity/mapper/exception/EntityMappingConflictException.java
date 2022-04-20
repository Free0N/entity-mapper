package org.samearch.jira.lib.entity.mapper.exception;

public class EntityMappingConflictException extends EntityMappingException {

    public EntityMappingConflictException(String existingMappingKey) {
        errorMessage = String.format("Mapping with key %s already exists", existingMappingKey);
    }

}
