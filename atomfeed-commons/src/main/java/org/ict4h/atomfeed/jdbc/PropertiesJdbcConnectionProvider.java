package org.ict4h.atomfeed.jdbc;

import org.ict4h.atomfeed.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class PropertiesJdbcConnectionProvider implements JdbcConnectionProvider {
    @Override
	public Connection getConnection() throws SQLException {
        Configuration configuration = Configuration.getInstance();
        return DriverManager.getConnection(
                configuration.getJdbcUrl(),
                configuration.getJdbcUsername(),
                configuration.getJdbcPassword()
        );
	}
}
