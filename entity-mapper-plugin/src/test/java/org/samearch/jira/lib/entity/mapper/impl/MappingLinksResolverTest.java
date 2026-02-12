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

package org.samearch.jira.lib.entity.mapper.impl;

import org.junit.Before;
import org.junit.Test;
import org.samearch.jira.lib.entity.mapper.api.EntityMapping;
import org.samearch.jira.lib.entity.mapper.api.exception.ClosedChainEntityMappingException;
import org.samearch.jira.lib.entity.mapper.impl.mapping.EntityMappingStorage;
import org.samearch.jira.lib.entity.mapper.impl.mapping.MappingLinksResolver;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class MappingLinksResolverTest {

    private EntityMappingStorage entityMappingStorage;
    private MappingLinksResolver mappingLinksResolver;

    @Before
    public void setUp() {
        entityMappingStorage = new InMemoryEntityMappingStorage();
        mappingLinksResolver = new MappingLinksResolver(entityMappingStorage);
    }

    /**
     * Проверка резолвинга нормальной цепочки маппингов.
     */
    @Test
    public void testRecursiveLinkResoling() throws ClosedChainEntityMappingException {
        String sourceMappingKey = "service.atlassian.support.project.id";
        String destinationMappingKey = "system.project.AD.id";
        String destinationMappingValue = "12345";
        fillMappingManagerWithLinkedValues(entityMappingStorage, sourceMappingKey, destinationMappingKey, destinationMappingValue, 5);

        String resolvedDestinationMappingKey = mappingLinksResolver.resolveTargetMappingKey(sourceMappingKey);

        assertEquals(destinationMappingKey, resolvedDestinationMappingKey);
    }

    /**
     * Проверка резолвинга для несуществующего ключа.<br/>
     * Если для резолвинга передали ключ, для которого отсутствует маппинг, то резолвер должен возвратить тот же самый ключ.
     */
    @Test
    public void testRecursiveLinkResolving_forNonExistingKey() throws ClosedChainEntityMappingException {
        String nonExistingKey = generateRandomMappingKey();
        String resolvedDestinationMappingKey = mappingLinksResolver.resolveTargetMappingKey(nonExistingKey);

        assertEquals(nonExistingKey, resolvedDestinationMappingKey);
    }

    /**
     * Проверка резолвинга маппингов с циклической ссылкой.
     */
    @Test(expected = ClosedChainEntityMappingException.class)
    public void testRecursiveLinkResolving_withClosedChain() throws ClosedChainEntityMappingException {
        String firstMappingKey = generateRandomMappingKey();
        String secondMappingKey = generateRandomMappingKey();
        String thirdMappingKey = generateRandomMappingKey();

        entityMappingStorage.createEntityMapping(firstMappingKey, buildLinkForKey(secondMappingKey));
        entityMappingStorage.createEntityMapping(secondMappingKey, buildLinkForKey(thirdMappingKey));
        entityMappingStorage.createEntityMapping(thirdMappingKey, buildLinkForKey(firstMappingKey));

        mappingLinksResolver.resolveTargetMappingKey(firstMappingKey);
    }

    /**
     * Проверка резолвинга ссылки на несуществующий ключ.<br/>
     * При правильной обработке MappingLinkResolver должен вернуть незамапленный ключ и не упасть с NPE.
     */
    @Test
    public void test_withNonExistingTargetKey() throws ClosedChainEntityMappingException {
        String firstMappingKey = generateRandomMappingKey();
        String secondMappingKey = generateRandomMappingKey();

        entityMappingStorage.createEntityMapping(firstMappingKey, buildLinkForKey(secondMappingKey));

        String resolvedDestinationKey = mappingLinksResolver.resolveTargetMappingKey(firstMappingKey);

        assertEquals(secondMappingKey, resolvedDestinationKey);
    }

    /**
     * Заполняет хранилище слинкованными маппингами.
     *
     * @param mappingManager заполняемое хранилище
     * @param sourceMappingKey начальный ключ цепочки
     * @param destinationMappingKey конечный ключ цепочки
     * @param destinationMappingValue замапленное значение для конечного ключа цепочки
     * @param linksChainLength длина цепочки (с учетом начального и конечного маппингов)
     */
    private void fillMappingManagerWithLinkedValues(
            EntityMappingStorage mappingManager,
            String sourceMappingKey,
            String destinationMappingKey,
            String destinationMappingValue,
            int linksChainLength) {
        EntityMapping headMapping = new EntityMapping();
        headMapping.setKey(sourceMappingKey);

        EntityMapping currentMapping = headMapping;

        String nextMappingKey = null;
        for (int i = 0; i < linksChainLength - 3; i++) {
            nextMappingKey = generateRandomMappingKey();
            String currentMappingValue = buildLinkForKey(nextMappingKey);
            currentMapping.setValue(currentMappingValue);

            mappingManager.createEntityMapping(currentMapping.getKey(), currentMapping.getValue());

            currentMapping = new EntityMapping();
            currentMapping.setKey(nextMappingKey);
        }

        EntityMapping preLastMapping = new EntityMapping();
        preLastMapping.setKey(nextMappingKey);
        preLastMapping.setValue(buildLinkForKey(destinationMappingKey));
        mappingManager.createEntityMapping(preLastMapping.getKey(), preLastMapping.getValue());

        EntityMapping lastMapping = new EntityMapping();
        lastMapping.setKey(destinationMappingKey);
        lastMapping.setValue(destinationMappingValue);
        mappingManager.createEntityMapping(lastMapping.getKey(), lastMapping.getValue());
    }

    /**
     * Генерирует случайную строку символов (A-Za-z0-9)<br/>
     * Взято со страницы https://www.baeldung.com/java-random-string
     */
    private String generateRandomMappingKey() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * Генерирует строку со ссылкой на указанный ключ.
     */
    private String buildLinkForKey(String key) {
        return String.format("${%s}", key);
    }
}
