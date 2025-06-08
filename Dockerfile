
FROM eclipse-temurin:21.0.2_13-jdk-jammy AS build

ARG JAR_FILE
WORKDIR /build

COPY $JAR_FILE application.jar
RUN java -Djarmode=layertools -jar application.jar extract --destination extracted


FROM eclipse-temurin:17-jdk-jammy

RUN addgroup --system spring-boot-group && adduser --system --ingroup spring-boot-group spring-boot
USER spring-boot:spring-boot-group

WORKDIR /application
VOLUME /tmp

COPY --from=build /build/extracted/dependencies ./
COPY --from=build /build/extracted/spring-boot-loader ./
COPY --from=build /build/extracted/snapshot-dependencies ./
COPY --from=build /build/extracted/application ./

ENTRYPOINT exec java ${JAVA_OPTS} org.springframework.boot.loader.launch.JarLauncher ${0} ${@}
