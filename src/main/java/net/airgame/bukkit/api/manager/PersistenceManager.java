package net.airgame.bukkit.api.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.airgame.bukkit.api.AirGamePlugin;
import net.airgame.bukkit.api.object.SimpleDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 持久化管理器
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class PersistenceManager {
    private static final Gson gson = new GsonBuilder().create();
    private static final JsonParser parser = new JsonParser();

    private static DataSource dataSource;

    public PersistenceManager(AirGamePlugin plugin) {
        FileConfiguration config = plugin.getConfig();

        File file = new File(plugin.getDataFolder(), "sql.properties");
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        } catch (Exception e) {
            AirGamePlugin.getLogUtils().error(e, "初始化数据库连接池时遇到了一个错误: ");
            return;
        }

        if (config.getBoolean("datasource.hikariCP") && initHikariCP(properties)) {
            AirGamePlugin.getLogUtils().info("已使用 HikariCP 作为数据库连接池!");
        } else {
            try {
                dataSource = new SimpleDataSource(properties);
                AirGamePlugin.getLogUtils().info("已使用 SimpleDataSource 作为数据库连接池!");
            } catch (ClassNotFoundException e) {
                AirGamePlugin.getLogUtils().error(e, "初始化数据库连接池时遇到了一个错误: ");
            }
        }
    }

    public static Gson getGson() {
        return gson;
    }

    public static JsonParser getParser() {
        return parser;
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public boolean initHikariCP(Properties properties) {
        try {
            Class.forName("com.zaxxer.hikari.HikariDataSource");
            HikariConfig config = new HikariConfig(properties);
            dataSource = new HikariDataSource(config);
            return true;
        } catch (ClassNotFoundException e) {
            AirGamePlugin.getLogUtils().warning("未找到 HikariCP 前置依赖, 使用默认连接池!");
        }
        return false;
    }

    public void close() {
        AirGamePlugin.getLogUtils().info("正在关闭数据库连接池.");
        try {
            Method method = dataSource.getClass().getMethod("close");
            if (!Modifier.isPublic(method.getModifiers())) {
                return;
            }
            method.invoke(dataSource);
        } catch (Exception | Error ignored) {
        }
        AirGamePlugin.getLogUtils().info("数据库连接池关闭成功.");
    }
}
