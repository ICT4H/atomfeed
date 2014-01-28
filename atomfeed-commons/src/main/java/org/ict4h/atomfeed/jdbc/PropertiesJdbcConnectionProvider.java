package org.ict4h.atomfeed.jdbc;

import org.ict4h.atomfeed.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class PropertiesJdbcConnectionProvider implements JdbcConnectionProvider {

    private Connection connection;

    @Override
	public Connection getConnection() throws SQLException {
        Configuration configuration = Configuration.getInstance();
        connection = DriverManager.getConnection(
                configuration.getJdbcUrl(),
                configuration.getJdbcUsername(),
                configuration.getJdbcPassword()
        );
        return connection;
	}

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        connection.close();
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
                throw new RuntimeException("Connection is null or closed");
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }    }

    @Override
    public void rollback() {
        try {
            if (connection == null || connection.isClosed()) {
                throw new RuntimeException("Connection is null or closed");
            }
            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
