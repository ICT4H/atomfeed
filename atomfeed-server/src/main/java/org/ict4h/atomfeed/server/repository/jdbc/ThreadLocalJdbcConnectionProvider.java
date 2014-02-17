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
            Connection connection = getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        try {
            Connection connection = threadConnection.get();
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
            Connection connection = threadConnection.get();
            if (connection == null || connection.isClosed()) {
                throw new RuntimeException("Cannot rollback .Connection is null or closed");
            }
            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}