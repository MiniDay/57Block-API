package net.airgame.bukkit.api.sql;

import net.airgame.bukkit.api.AirGameAPI;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

@SuppressWarnings("RedundantThrows")
public class SimpleDataSource implements DataSource {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    private int loginTimeout;

    public SimpleDataSource(Properties properties) throws ClassNotFoundException {
        Class.forName(properties.getProperty("driverClassName", "com.mysql.jdbc.Driver"));
        jdbcUrl = properties.getProperty("jdbcUrl");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
        AirGameAPI.getLogUtils().info("连接池最大连接数: %s", properties.getProperty("maximumPoolSize"));
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    @Override
    public <T> T unwrap(Class<T> clazz) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> clazz) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return AirGameAPI.getLogUtils().getFileWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
    }

    @Override
    public int getLoginTimeout() {
        return loginTimeout;
    }

    @Override
    public void setLoginTimeout(int loginTimeout) {
        this.loginTimeout = loginTimeout;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return AirGameAPI.getLogUtils().getLogger();
    }
}
