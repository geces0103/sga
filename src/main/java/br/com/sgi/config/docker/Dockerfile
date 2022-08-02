FROM adoptopenjdk/openjdk11:alpine

# App Opções
ENV APP_NAME="sgi" \
    APP_HOME=/opt/$APP_NAME \
    JAVA_OPTIONS=""

ADD ./src/main/docker/src/java.security $JAVA_HOME/conf/security/java.security

COPY ./src/main/docker/src/fullprofile.jfc $JAVA_HOME/lib/jfr/fullprofile.jfc

COPY ./src/main/docker/src/java-flight-recorder.sh /opt/jfr.sh

RUN wget -qO /opt/arthas-boot.jar https://alibaba.github.io/arthas/arthas-boot.jar && \
    chmod +x /opt/jfr.sh && \
    chmod +x /opt/arthas-boot.jar && \
    mkdir -p $APP_HOME && \
    rm -rf /tmp/* /var/cache/apk/*

# Criando rotina para HealthCheck do container
HEALTHCHECK --start-period=15s --interval=1m --timeout=15s --retries=5 \
    CMD `[[ $(wget -qO- localhost:8080/${APP_NAME}/actuator/health | grep -o -E '"status":"[^"]*"' | awk -F\: '{print $2}') = "\"UP\"" ]] || exit 1`

# Copia FatJar da Aplicação
COPY ./build/libs/${APP_NAME}*.jar /opt/${APP_NAME}.jar

CMD java \
    -Duser.timezone=America/Fortaleza \
    -Duser.language=pt \
    -Duser.region=BR \
    -Duser.country=BR \
    -Xms512m \
    -Xmx4096m \
    -XX:+PreserveFramePointer \
    -jar /opt/$APP_NAME.jar
