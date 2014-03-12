package org.ict4h.atomfeed.server.repository.jdbc;

import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ThreadLocalJdbcConnectionProvider implements JdbcConnectionProvider {
    private  final ThreadLocal<Connection> threadConnection = new ThreadLocal();

    private DataSource dataSource;

    public ThreadLocalJdbcConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection existingConnection = threadConnection.get();
        if (existingConnection != null && !existingConnection.isClosed())
            return existingConnection;

        Connection connection = dataSource.getConnection();
        threadConnection.set(connection);
        return connection;
    }

}
