package org.samearch.jira.lib.entity.mapper.ui.rest;

import com.atlassian.jira.user.ApplicationUser;
import org.apache.commons.lang3.StringUtils;
import org.samearch.jira.lib.entity.mapper.EntityMapper;
import org.samearch.jira.lib.entity.mapper.EntityMapping;
import org.samearch.jira.lib.entity.mapper.exception.EntityMappingConflictException;
import org.samearch.jira.lib.entity.mapper.exception.EntityMappingNotFoundException;
import org.samearch.jira.lib.entity.mapper.ui.rest.dto.EntityMappingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Набор высокоуровневых утилит, применяемых в реализациях REST endpoin-ов.
 */
@Component
class EntityMappingRestUtils {

    private final EntityMapper entityMapper;

    @Autowired
    public EntityMappingRestUtils(EntityMapper entityMapper) {
        this.entityMapper = entityMapper;
    }

    /**
     * Обновляет состояние маппинга на основании данных, содержащихся в {@code updateRequestData}.<br/>
     * <br/>
     * При обновлении состояния маппинга учитывается то, что ни ключ, ни значение маппинга не могут быть пустыми. Т.е.
     * если в {@code updateRequestData} какое-либо значение (для {@code key} или {@code value}) равно {@code null}, то
     * считается, что эта часть состояния маппинга не требует обновления.
     *
     * @param mappingId идентификатор обновляемого маппинга
     * @param updateRequestData данные для обновления ключа и/или значения маппинга
     * @return обновленное состояние маппинга
     * @throws EntityMappingNotFoundException в случае, когда маппинга с указанным идентификатором не существует
     * @throws EntityMappingConflictException в случае, когда обновляет ключ маппинга, а для нового ключа уже существует запись
     */
    public EntityMapping updateEntityMappingFromRestRequest(ApplicationUser user, int mappingId, EntityMappingDto updateRequestData)
            throws EntityMappingNotFoundException, EntityMappingConflictException {

        Optional<EntityMapping> currentEntityMappingHolder = entityMapper.getMapping(mappingId);
        if (!currentEntityMappingHolder.isPresent()) {
            throw new EntityMappingNotFoundException(mappingId);
        }

        final EntityMapping currentEntityMapping = currentEntityMappingHolder.get();
        final EntityMapping updatedEntityMappingData = constructUpdateMappingObject(currentEntityMapping, updateRequestData);

        return entityMapper.updateMapping(user, mappingId, updatedEntityMappingData.getKey(), updatedEntityMappingData.getValue());

    }

    /**
     * Конструирует запись маппинга обновляя данные существующей записи.
     *
     * @param currentMappingState Существующая запись маппинга. Исходные данные конструируемой записи.
     * @param mappingUpdateRequestData Новые данные для существующей записи, пришедшие из REST-запроса или другого источника.
     */
    private EntityMapping constructUpdateMappingObject(
            final EntityMapping currentMappingState,
            final EntityMappingDto mappingUpdateRequestData) {

        EntityMapping updatedEntityMapping = new EntityMapping();
        updatedEntityMapping.setId(currentMappingState.getId());

        final String newMappingKey = mappingUpdateRequestData.getKey();
        if (StringUtils.isNotBlank(newMappingKey)) {
            updatedEntityMapping.setKey(newMappingKey);
        } else {
            updatedEntityMapping.setKey(currentMappingState.getKey());
        }

        final String newMappingValue = mappingUpdateRequestData.getValue();
        if (StringUtils.isNotBlank(newMappingValue)) {
            updatedEntityMapping.setValue(newMappingValue);
        } else {
            updatedEntityMapping.setValue(currentMappingState.getValue());
        }

        return updatedEntityMapping;

    }

}
