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
COPY target/user-service-${APP_VERSION}.jar /app/

# 复制启动脚本
COPY script/startup.sh /app/
RUN chmod +x /app/startup.sh

# 安装 curl
RUN apk add --no-cache curl

# 将宿主机的Maven本地仓库复制到容器
VOLUME /app/config

# 暴露端口，如果你的应用使用了特定的端口，请在这里修改
EXPOSE 8080

# 启动应用
# RUN 用于安装应用程序和包，并创建新的镜像层
# 如果Docker镜像的目的是运行一个应用程序或服务，例如运行 MySQL，那么应该优先使用 Exec 格式的 ENTRYPOINT 命令。CMD 可以为 ENTRYPOINT 提供额外的默认参数，并且这些默认参数可以被 docker run 命令行替换。
# 如果你想为容器设置默认的启动命令，可以使用 CMD 命令。用户可以在 docker run 命令行中覆盖这个默认命令。
#CMD ["java", "-jar", "/app/user-service-0.0.1-SNAPSHOT.jar", "/app/config/application.yml"]
ENTRYPOINT ["/app/startup.sh"]
# 在容器启动时执行 top 命令，并以批处理模式（-b参数）执行。这意味着 top 命令不会进入交互式模式，而是会以非交互方式显示系统的实时进程信息。这对于监控容器资源使用情况非常有用。
#ENTRYPOINT ["top", "-b"]