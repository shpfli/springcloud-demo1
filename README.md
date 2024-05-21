# springcloud-demo1

+ eureka-server 是一个简单的注册中心，实现了服务注册和服务发现；
+ eureka-service 是一个简单的服务提供者，提供了User的增删改查功能；
+ eureka-client 是一个简单的服务消费者，通过服务名调用服务提供者的接口；

## 项目启动

+ 启动eureka-server : 执行 pers.hubery.eurekaserver.EurekaServerApplication 类的 main 方法
+ 启动eureka-service : 执行 pers.hubery.userservice.UserServiceApplication 类的 main 方法
+ 启动eureka-client : 执行 pers.hubery.userclient.UserClientApplication 类的 main 方法

### 验证

+ 访问 http://localhost:8761/ 可以看到eureka-server的页面
+ 访问 http://localhost:8761/eureka/apps 可以看到eureka-server的注册列表
+ 访问 http://localhost:8080/users 可以查看 user-service 的用户列表
+ 访问 http://localhost:8081/users 可以查看 user-client 通过服务名调用 user-service 的接口查询的用户列表

