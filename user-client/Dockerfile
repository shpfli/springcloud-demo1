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
COPY target/user-client-${APP_VERSION}.jar /app/

# 复制启动脚本
COPY script/startup.sh /app/
RUN chmod +x /app/startup.sh

# 将宿主机的Maven本地仓库复制到容器
VOLUME /app/config

# 暴露端口，如果你的应用使用了特定的端口，请在这里修改
EXPOSE 8080

# 启动应用
ENTRYPOINT ["/app/startup.sh"]