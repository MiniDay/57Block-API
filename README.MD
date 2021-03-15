# AirGame-API

五彩方块服务器的 API

**本项目仍在开发阶段，许多类和方法都不稳定，请谨慎考虑使用该API**

# 使用方法

## 对于服主

1. 下载AirGame-API
2. 将插件扔进 plugins 文件夹中
3. 重启服务器
4. 按照自己的需要修改 `plugins/AirGame-API/config.yml`
5. 将插件需要的依赖扔进 `plugins/AirGame-API/libs` 中（如果没有则不用）
6. 再次重启服务器
7. 完成

## 对于开发者

1. 添加 maven 仓库

```xml

<repository>
    <id>MiniDay</id>
    <url>http://maven.hamster3.cn/repository/miniday-repo/</url>
</repository>

```

2. 添加 maven 依赖

```xml

<dependency>
    <groupId>net.airgame.bukkit</groupId>
    <artifactId>AirGame-API</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>

```

3. 开始写代码...

# todo list

- 完善 CommandAPI
  - 让添加 static 方法的 CommandExecutor 可以无需构造对象（可能可以改善性能）
  - 添加一个 Help 方法，当所有命令均未成功匹配参数时执行该方法
  - 添加一个默认 Help 方法用于展示 Command 用法

# 已完成的功能

## 日志记录器

使用方法待补全...

[LogUtils](src/main/java/net/airgame/bukkit/api/util/LogUtils.java)

## 方便易用的多语言消息系统

使用方法待补全...

## 方便易用的箱子 GUI 系统

如果要把 yml 文件放在 Java 包中（而不是 resources 文件夹下）的话  
请在`pom.xml` 的 `build` 节点中添加以下设定:

```xml

<resources>
    <resource>
        <directory>src/main/resources</directory>
        <filtering>true</filtering>
    </resource>
    <resource>
        <directory>src/main/java</directory>
        <filtering>true</filtering>
        <includes>
            <include>**/*.yml</include>
        </includes>
    </resource>
</resources>
```

使用方法待补全...

## 基于注解的命令执行器

使用方法待补全...

### 参数匹配

- String
- Byte
- Integer
- Long
- Double
- Boolean
- Location
- World
- OfflinePlayer
- Player
- BlockFace
