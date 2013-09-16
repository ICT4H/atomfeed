package org.ict4h.atomfeed.client.repository;

import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SpringJdbcConnectionProvider implements JdbcConnectionProvider {

    private DataSource dataSource;

    public SpringJdbcConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DataSourceUtils.doGetConnection(dataSource);
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }

}
