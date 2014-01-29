package org.ict4h.atomfeed.server.repository.jdbc;

import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ThreadLocalJdbcConnectionProvider implements JdbcConnectionProvider {
    private  final ThreadLocal<Connection> threadConnection = new ThreadLocal();

    private DataSource dataSource;
    private Connection connection;

    public ThreadLocalJdbcConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection existingConnection = threadConnection.get();
        if (existingConnection != null && !existingConnection.isClosed())
            return existingConnection;

        Connection newConnection = dataSource.getConnection();
        threadConnection.set(newConnection);

        connection = threadConnection.get();
        return connection;
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        try {
            connection.close();
        } finally {
            threadConnection.remove();
        }
    }

    @Override
    public void startTransaction() {
        try {
            if (connection == null || connection.isClosed()) {
                getConnection();
            }
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        try {
            if (connection == null || connection.isClosed()) {
                throw new RuntimeException("Cannot commit.Connection is null or closed");
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }    }

    @Override
    public void rollback() {
        try {
            if (connection == null || connection.isClosed()) {
                throw new RuntimeException("Cannot rollback .Connection is null or closed");
            }
            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
