package net.airgame.bukkit.api.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.UtilityElf;
import net.airgame.bukkit.api.PluginMain;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 持久化管理器
 */
public class PersistenceManager {
    private static DataSource dataSource;

    public static void init() {
        try {
            PluginMain.getLogUtils().info("开始初始化持久化管理器.");
            File file = new File(PluginMain.getInstance().getDataFolder(), "sql.properties");
            Properties properties = new Properties();
            properties.load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            HikariConfig config = new HikariConfig(properties);
            dataSource = new HikariDataSource(config);
            PluginMain.getLogUtils().info("持久化管理器初始化完成.");
        } catch (Exception e) {
            PluginMain.getLogUtils().error(e, "持久化管理器初始化时遇到了一个错误: ");
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
