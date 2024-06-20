#!/bin/sh
#set -x
#******************************************************************************
# @file    : entrypoint.sh
# @author  : hubery
# @date    : 2024-06-20
#
# @brief   : entry point for manage service start order
# history  : init
#******************************************************************************

: "${SLEEP_SECOND:=2}"

wait_for() {
    echo Waiting for "$1" to listen on "$2"...
    while ! nc -z "$1" "$2"; do echo waiting...; sleep $SLEEP_SECOND; done
}

#declare DEPENDS
#declare CMD

while getopts "d:c:" arg
do
    case $arg in
        d)
            DEPENDS=$OPTARG
            ;;
        c)
            CMD=$OPTARG
            ;;
        ?)
            echo "unknown argument"
            exit 1
            ;;
    esac
done


# 保存原来的IFS值
OLD_IFS="$IFS"

# 设置IFS为冒号
IFS=':'

# 使用read命令来读取分割后的字符串到变量a和b
# read命令默认会按照IFS中的分隔符来分割输入
# 这里我们使用-r选项来禁止反斜杠转义
read -r host port << EOF
"$DEPENDS"
EOF

# 恢复IFS到原来的值
IFS="$OLD_IFS"
wait_for "$host" "$port"

#for var in ${DEPENDS//,/ }
#do
#    host=${var%:*}
#    port=${var#*:}
#    wait_for $host $port
#done

if [ -z "$CMD" ]; then
    CMD="java -jar /app/user-service-0.0.1-SNAPSHOT.jar /app/config/application.yml"
fi

eval "$CMD"