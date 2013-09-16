package org.ict4h.atomfeed.server.repository.jdbc;

import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ThreadLocalJdbcConnectionProvider implements JdbcConnectionProvider {
    private static final ThreadLocal<Connection> threadConnection = new ThreadLocal();

    private DataSource dataSource;

    public ThreadLocalJdbcConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection existingConnection = threadConnection.get();
        if (existingConnection != null)
            return existingConnection;

        Connection newConnection = dataSource.getConnection();
        threadConnection.set(newConnection);

        return threadConnection.get();
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        connection.close();
        threadConnection.remove();
    }

}
