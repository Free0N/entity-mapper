package org.samearch.jira.lib.entity.mapper.exception;

public class EntityMappingException extends Exception {

    protected String errorMessage;

    public EntityMappingException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public EntityMappingException() {
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

}
