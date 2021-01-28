package net.airgame.bukkit.api.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.airgame.bukkit.api.AirGameAPI;
import net.airgame.bukkit.api.sql.SimpleDataSource;

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
    private static DataSource dataSource;

    public PersistenceManager(boolean hikariCP) {
        File file = new File(AirGameAPI.getInstance().getDataFolder(), "sql.properties");
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        } catch (Exception e) {
            AirGameAPI.getLogUtils().error(e, "初始化数据库连接池时遇到了一个错误: ");
            return;
        }

        if (hikariCP && initHikariCP(properties)) {
            AirGameAPI.getLogUtils().info("已使用 HikariCP 作为数据库连接池!");
        } else {
            try {
                dataSource = new SimpleDataSource(properties);
                AirGameAPI.getLogUtils().info("已使用 SimpleDataSource 作为数据库连接池!");
            } catch (ClassNotFoundException e) {
                AirGameAPI.getLogUtils().error(e, "初始化数据库连接池时遇到了一个错误: ");
            }
        }
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
            AirGameAPI.getLogUtils().warning("未找到 HikariCP 前置依赖, 使用默认连接池!");
        }
        return false;
    }

    public void close() {
        AirGameAPI.getLogUtils().info("正在关闭数据库连接池.");
        try {
            Method method = dataSource.getClass().getMethod("close");
            if (!Modifier.isPublic(method.getModifiers())) {
                return;
            }
            method.invoke(dataSource);
        } catch (Exception | Error ignored) {
        }
        AirGameAPI.getLogUtils().info("数据库连接池关闭成功.");
    }
}
