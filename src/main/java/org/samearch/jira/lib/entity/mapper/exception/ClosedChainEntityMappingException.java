package org.samearch.jira.lib.entity.mapper.exception;

import java.util.TreeSet;

/**
 * Генерируется в том случае, когда в конфигурации существуют циклические ссылки.
 */
public class ClosedChainEntityMappingException extends EntityMappingException {

    public ClosedChainEntityMappingException(TreeSet<String> keyChain) {

        String errorMessageHeader = "Closed chain links found:";
        String keyChainMessagePart = String.join(" > ", keyChain);
        this.errorMessage = String.format("%s %s", errorMessageHeader, keyChainMessagePart);

    }

}
