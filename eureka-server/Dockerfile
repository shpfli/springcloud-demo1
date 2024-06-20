# 使用官方提供的Java 8基础镜像
FROM openjdk:8-jdk-alpine
LABEL authors="hubery"

# 定义构建参数，默认值为1.0.0
ARG VERSION=1.0.0

# 使用构建参数来设置环境变量
ENV APP_VERSION=${VERSION}

# 设置工作目录
WORKDIR /app

# 将你的应用的jar包复制到工作目录
COPY target/eureka-server-${APP_VERSION}.jar /app/
#COPY target/eureka-server-0.0.1-SNAPSHOT.jar /app/eureka-server-0.0.1-SNAPSHOT.jar

# 挂载配置目录
#VOLUME /app/config

# 安装 curl
RUN apk add --no-cache curl

# 暴露 8761 端口
EXPOSE 8761

# 设置容器启动时运行的命令
#CMD ["java", "-jar", "/app/eureka-server-0.0.1-SNAPSHOT.jar"]
#ENTRYPOINT ["/bin/sh", "-c", "echo $EUREKA_VERSION"]
ENTRYPOINT ["/bin/sh", "-c", "env && java -jar /app/eureka-server-${APP_VERSION}.jar"]
#ENTRYPOINT  ["java", "-jar", "/app/eureka-server-${EUREKA_VERSION}.jar"]
#ENTRYPOINT  ["java", "-jar", "/app/eureka-server-0.0.1-SNAPSHOT.jar"]