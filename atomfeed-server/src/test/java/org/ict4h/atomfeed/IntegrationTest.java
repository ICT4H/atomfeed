package org.ict4h.atomfeed;


import org.ict4h.atomfeed.server.repository.jdbc.JdbcConnectionProvider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public abstract class IntegrationTest {
    protected Connection getConnection() throws SQLException {
        ResourceBundle bundle = ResourceBundle.getBundle("atomfeed");
        return DriverManager.getConnection(bundle.getString("jdbc.url"),
                bundle.getString("jdbc.username"),
                bundle.getString("jdbc.password")
        );
    }

    protected JdbcConnectionProvider getProvider(final Connection connection){
        return new JdbcConnectionProvider() {
            @Override
            public Connection getConnection() throws SQLException {
                return connection;
            }
        };
    }
}