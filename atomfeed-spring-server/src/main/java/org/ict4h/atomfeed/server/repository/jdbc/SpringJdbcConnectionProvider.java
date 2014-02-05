package org.ict4h.atomfeed.server.repository.jdbc;

import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SpringJdbcConnectionProvider implements JdbcConnectionProvider {

    private DataSource dataSource;
    private Connection connection;

    public SpringJdbcConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        connection = DataSourceUtils.doGetConnection(dataSource);
        return connection;
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }


    @Override
    public void startTransaction() {
        try {
            if (connection == null) {
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
            if (connection == null) {
                getConnection();
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }    }

    @Override
    public void rollback() {
        try {
            if (connection == null) {
                getConnection();
            }
            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
