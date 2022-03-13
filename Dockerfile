FROM 172.26.128.118:8085/yxdd/jdk:alpine-3.11_glibc-2.31-jdk8202
ARG JAR_FILE
ENV PROFILE default
ADD target/${JAR_FILE} /opt/weather.jar
EXPOSE 8902
ENTRYPOINT java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -Duser.timezone=Asia/Shanghai -Dfile.encoding=UTF-8 -Dspring.cloud.nacos.discovery.namespace=${PROFILE} -Dspring.cloud.nacos.config.namespace=${PROFILE} -jar /opt/weather.jar