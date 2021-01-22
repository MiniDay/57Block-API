package net.airgame.bukkit.api.sql;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

// todo 完成这个类
@SuppressWarnings("RedundantThrows")
public class SimpleDataSource implements DataSource {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public SimpleDataSource(Properties properties) throws ClassNotFoundException {
        jdbcUrl = properties.getProperty("jdbcUrl");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
        Class.forName(properties.getProperty("driverClassName", "com.mysql.jdbc.Driver"));
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
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
