package net._57block.bukkit.api.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * 日志记录器
 */
@SuppressWarnings({"unused"})
public class LogUtils {
    public static YamlConfiguration DEFAULT_CONFIG;

    private final Plugin plugin;

    private Logger logger;
    private PrintWriter fileWriter;
    private SimpleDateFormat dateFormat;

    private boolean debug;

    /**
     * 以默认配置实例化一个日志记录器
     * 默认的配置在 plugins/57block-API/defaultLogSettings.yml 中
     *
     * @param plugin 使用这个日志记录器的插件对象
     */
    public LogUtils(@NotNull Plugin plugin) {
        this.plugin = plugin;
        File logSettingsFile = new File(plugin.getDataFolder(), "logSettings.yml");
        if (logSettingsFile.exists()) {
            init(YamlConfiguration.loadConfiguration(logSettingsFile));
        } else {
            init(DEFAULT_CONFIG);
        }
    }

    /**
     * 以特定配置文件实例化一个日志记录器
     *
     * @param plugin 使用这个日志记录器的插件对象
     * @param config 日志记录器的配置文件
     */
    public LogUtils(@NotNull Plugin plugin, @NotNull ConfigurationSection config) {
        this.plugin = plugin;
        init(config);
    }

    /**
     * 初始化这个日志器
     *
     * @param config 配置对象
     */
    @SuppressWarnings("ConstantConditions")
    private void init(@NotNull ConfigurationSection config) {
        if (config.getBoolean("usePluginLogger", false)) {
            logger = plugin.getLogger();
            info("使用Bukkit自带日志器, 日志将会打印至控制台.");
        }
        debug = config.getBoolean("debug", false);
        if (debug) {
            info("已启用调试信息输出.");
        } else {
            info("已关闭调试信息输出.");
        }
        if (!config.getBoolean("logSaveToFile", true)) {
            warning("未启用文件存储, 日志将不会保存至磁盘!");
            return;
        }

        File logFolder;
        if (config.contains("logFolder")) {
            File logFolderLocation = new File(config.getString("logFolder"));
            if (logFolderLocation.exists() || logFolderLocation.mkdirs()) {
                logFolder = new File(logFolderLocation, plugin.getName());
            } else {
                warning("配置文件中的日志存储位置不可用, 更改至插件目录内...");
                logFolder = new File(plugin.getDataFolder(), "logs");
            }
        } else {
            logFolder = new File(plugin.getDataFolder(), "logs");
        }
        info("日志存储位置为: " + logFolder.getAbsolutePath());

        if (logFolder.mkdirs()) {
            info("创建日志存档文件夹...");
        }

        long now = System.currentTimeMillis();
        try {
            String time = String.valueOf(now);
            File file = new File(logFolder, time + ".log");
            int i = 1;
            // 如果文件名已被占用则在后面加一个 -n
            while (file.exists()) {
                file = new File(logFolder, time + "-" + i + ".log");
                i++;
            }
            if (config.getBoolean("bufferWriter", true)) {
                fileWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)));
            } else {
                fileWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            error(e, "初始化日志器时出现了一个错误: ");
        }
        dateFormat = new SimpleDateFormat(config.getString("dateFormat", "yyyy-MM-dd HH:mm:ss:SSS"));

        info("日志器初始化完成...");
        info("当前时间戳: " + now);
        info("当前日志输出信息: %s-%s", plugin.getName(), plugin.getDescription().getVersion());

        int saveDays = config.getInt("saveDays", 7);
        if (saveDays < 1) {
            return;
        }
        info("日志存储有效期为 %d 天.", saveDays);
        for (File file : logFolder.listFiles()) {
            if (now - file.lastModified() > 86400 * 1000 * 7) {
                if (file.delete()) {
                    info("日志文件 %s 由于超过存储期限而被删除.", file.getName());
                }
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }));
    }

    /**
     * 记录一条信息
     *
     * @param info 信息
     */
    public void info(@NotNull String info) {
        if (logger != null) {
            logger.info(info);
        }
        if (fileWriter != null) {
            fileWriter.format(
                    "[%s] [INFO] %s\n",
                    dateFormat.format(new Date()),
                    info
            );
        }
    }

    /**
     * 记录一条信息，使用 String.format() 替换参数
     *
     * @param info   信息
     * @param params 参数
     * @see String#format(String, Object...)
     */
    public void info(@NotNull String info, @NotNull Object... params) {
        info(String.format(info, params));
    }

    /**
     * 记录一条警告
     *
     * @param warning 警告
     */
    public void warning(@NotNull String warning) {
        if (logger != null) {
            logger.warning(warning);
        }
        if (fileWriter != null) {
            fileWriter.format(
                    "[%s] [WARNING] %s\n",
                    dateFormat.format(new Date()),
                    warning
            );
        }
    }

    /**
     * 记录一条警告，使用 String.format() 替换参数
     *
     * @param warning 警告
     * @param params  参数
     * @see String#format(String, Object...)
     */
    public void warning(@NotNull String warning, @NotNull Object... params) {
        warning(String.format(warning, params));
    }

    /**
     * 记录一条调试信息
     *
     * @param debug 试信息
     */
    public void debug(@NotNull String debug) {
        if (!this.debug) {
            return;
        }
        if (logger != null) {
            logger.info(debug);
        }
        if (fileWriter != null) {
            fileWriter.format(
                    "[%s] [DEBUG] %s\n",
                    dateFormat.format(new Date()),
                    debug
            );
        }
    }

    /**
     * 记录一条调试信息，使用 String.format() 替换参数
     *
     * @param debug  试信息
     * @param params 参数
     * @see String#format(String, Object...)
     */
    public void debug(@NotNull String debug, @NotNull Object... params) {
        debug(String.format(debug, params));
    }

    /**
     * 记录一个异常
     *
     * @param e 异常对象
     */
    public void error(@NotNull Exception e) {
        if (logger != null) {
            e.printStackTrace();
        }
        if (fileWriter != null) {
            e.printStackTrace(fileWriter);
        }
    }

    /**
     * 记录一个异常，附带描述消息
     *
     * @param e       异常对象
     * @param message 附加描述消息
     */
    public void error(@NotNull Exception e, @NotNull String message) {
        warning(message);
        error(e);
    }

    /**
     * 记录一个异常，附带描述消息，使用 String.format() 替换参数
     *
     * @param e       异常对象
     * @param message 附加描述消息
     * @param params  参数
     * @see String#format(String, Object...)
     */
    public void error(@NotNull Exception e, @NotNull String message, @NotNull Object... params) {
        warning(message, params);
        error(e);
    }

    /**
     * 将日志输出缓存立即清空
     */
    public void flush() {
        if (fileWriter != null) {
            fileWriter.flush();
        }
    }

    /**
     * 获取 Bukkit 插件自带的日志器对象
     *
     * @return 日志器对象
     */
    @NotNull
    public Logger getLogger() {
        return logger;
    }

    /**
     * 获取插件对象
     *
     * @return 插件对象
     */
    @NotNull
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * 获取 文件输出器对象
     *
     * @return 文件输出器对象
     */
    @Nullable
    public PrintWriter getFileWriter() {
        return fileWriter;
    }

    /**
     * 是否启用调试信息输出
     *
     * @return 是否启用调试信息输出
     */
    public boolean isDebug() {
        return debug;
    }
}
