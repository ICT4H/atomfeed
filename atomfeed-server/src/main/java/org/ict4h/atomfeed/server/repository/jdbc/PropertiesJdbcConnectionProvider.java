package org.ict4h.atomfeed.server.repository.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class PropertiesJdbcConnectionProvider implements JdbcConnectionProvider {
    @Override
	public Connection getConnection() throws SQLException {
        ResourceBundle bundle = ResourceBundle.getBundle("atomfeed");
        return DriverManager.getConnection(bundle.getString("jdbc.url"),
                bundle.getString("jdbc.username"),
                bundle.getString("jdbc.password")
        );
	}
}
