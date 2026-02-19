# Пример плагина, использующего Entity Mapper

В плагине реализована пост-функция, которая устанавливает исполнителем задач пользователя, логин которого указан
в маппингах.

Для корректной работы этого плагина требуется установленный плагин Entity Mapper соответствующей версии.

Для работы пост-функции требуется создать такой маппинг:
```text
project.AD.duty => admin
```
В этом маппинге нужно поменять `AD` на ключ существующего проекта, а `admin` на логин существующего пользователя.

Так же нужно добавить пост-функцию `Assign To Duty` на шаг создания задачи в соответствующем бизнес-процессе.

Так же можно использовать ссылки между ключами:
```text
project.AD.manager => admin
project.AD.duty => ${project.AD.manager}
```
В этом случае задача будет назначена так же на пользователя с логином `admin`

## Настройка проекта плагина для использования Entity Mapper-а

### Изменения в pom.xml

В файле настроек проекта плагина pom.xml требуется добавить зависимость:
```xml
<dependency>
    <groupId>org.samearch.jira.lib</groupId>
    <artifactId>entity-mapper-api</artifactId>
    <version>${version.mapping.plugin.api}</version>
    <scope>provided</scope>
</dependency>
```
Версия зависимости должна совпадать с major-версией установленного плагина. Например, если вы установили плагин
Entity Mapper версии `2.1.1`, то версия подключаемого артефакта должна быть `2`.

### Использование в коде плагина

После подключения зависимости к проекту в коде проекта станет доступен для использования интерфейс
`org.samearch.jira.lib.entity.mapper.api.EntityMapper`. Это основной интерфейс для взаимодействия с плагином
Entity Mapper.

Для использования EntityMapper его инстанс можно получить как зависимость в конструкторе:

_См. пример в [AssignToDuty](src/main/java/org/samearch/jira/lib/examples/wf/AssignToDuty.java)_

```java
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.samearch.jira.lib.entity.mapper.api.EntityMapper;
import org.springframework.stereotype.Service;

@Service
public class SomeService {

    @ComponentImport
    private final EntityMapper entityMapper;
    
    public SomeService(EntityMapper entityMapper) {
        this.entityMapper = entityMapper;
    }
}
```

Главное не забыть про аннотацию `ComponentImport`