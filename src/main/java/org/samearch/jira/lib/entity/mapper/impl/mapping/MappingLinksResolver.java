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
