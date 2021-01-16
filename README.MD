# 57Block-API

五彩方块服务器的 API

本项目以 [LGPL许可协议](LICENSE.md) 开源

# todo list

- 完善 CommandAPI
    - 让添加 static 方法的 CommandExecutor 可以无需构造对象（可能可以改善性能）
    - 添加一个 Help 方法，当所有命令均未成功匹配参数时执行该方法
    - 添加一个默认 Help 方法用于展示 Command 用法

# 方便易用的日志记录器

[LogUtils](src/main/java/net/_57block/bukkit/api/util/LogUtils.java)

# 方便易用的多语言消息系统

开发中...

# 方便易用的箱子 GUI 系统

开发中...

# 基于注解的命令执行器

已完成

## 参数匹配

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
