package org.samearch.jira.lib.entity.mapper.impl.mapping;

import org.samearch.jira.lib.entity.mapper.EntityMapping;
import org.samearch.jira.lib.entity.mapper.exception.ClosedChainEntityMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MappingLinksResolver {

    /**
     * Регулярное выражение, которому должно соответствовать значение маппинга, ссылающегося на другой маппинг:
     * ${other.mapping.key}
     */
    private static final Pattern MAPPING_LINK_PATTERN = Pattern.compile("^\\$\\{(?<linkTarget>[^}]+)}$");
    private static final String TARGET_MAPPING_PATTERN_GROUP_NAME = "linkTarget";

    private final EntityMappingStorage mappingManager;

    @Autowired
    public MappingLinksResolver(EntityMappingStorage mappingManager) {
        this.mappingManager = mappingManager;
    }

    /**
     * Возвращает ключ конечного маппинга.<br/>
     * По возвращенному ключу лежит не ссылка на какой-либо маппинг, а конкретное значение.
     *
     * @param sourceMappingKey исходный ключ маппинга
     */
    public String resolveTargetMappingKey(String sourceMappingKey) throws ClosedChainEntityMappingException {

        String linkTarget = sourceMappingKey;

        EntityMapping sourceMapping = mappingManager.getMappingForKey(linkTarget);

        if (sourceMapping == null) {
            return sourceMappingKey;
        }

        TreeSet<String> alreadyCheckedKeys = new TreeSet<>();

        Matcher mappingValueMatcher = MAPPING_LINK_PATTERN.matcher(sourceMapping.getValue());

        while (mappingValueMatcher.matches()) {
            linkTarget = mappingValueMatcher.group(TARGET_MAPPING_PATTERN_GROUP_NAME);

            if (isKeyAlreadyVisited(linkTarget, alreadyCheckedKeys)) {
                throw new ClosedChainEntityMappingException(alreadyCheckedKeys);
            } else {
                alreadyCheckedKeys.add(linkTarget);
            }

            EntityMapping destinationMapping = mappingManager.getMappingForKey(linkTarget);
            if (destinationMapping == null) {
                break;
            }

            String destinationMappingValue = destinationMapping.getValue();
            mappingValueMatcher = MAPPING_LINK_PATTERN.matcher(destinationMappingValue);
        }

        return linkTarget;

    }

    private boolean isKeyAlreadyVisited(String linkTarget, TreeSet<String> alreadyCheckedKeys) {
        return alreadyCheckedKeys.contains(linkTarget);
    }

}
