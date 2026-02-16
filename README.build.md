# Сборка плагина

Для сборки плагина требуется JDK версии не ниже 11 и Apache Maven (сборка тестировалась на версиях 3.9.8 и 3.9.12)

```shell
mvn clean package
```

Так же для сборки можно использовать образы Docker с maven-ом:

```shell
DOCKER_IMAGE="maven:3.9.12-amazoncorretto-25"
SRC_DIR=/usr/src/entity-mapper
# если есть время ждать загрузку зависимостей при каждой сборке
podman run -it --rm -v "${PWD}":${SRC_DIR} -w ${SRC_DIR} ${DOCKER_IMAGE} mvn clean package
# если нет времени ждать загрузку зависимостей при каждой сборке - зависимости загрузятся при первой сборке в .m2
# при сборке загружается ~140Mb pom- и jar-файлов
mkdir .m2
podman run -it --rm -v "${PWD}":${SRC_DIR} -v "${PWD}/.m2":/root/.m2 -w ${SRC_DIR} ${DOCKER_IMAGE} mvn clean package
```