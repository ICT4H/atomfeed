package org.ict4h.atomfeed;


import org.ict4h.atomfeed.jdbc.AtomFeedJdbcTransactionManager;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.transaction.AFTransactionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public abstract class IntegrationTest {
    private ResourceBundle bundle = ResourceBundle.getBundle("atomfeed");

    protected Connection getConnectionFromDriverManager() throws SQLException {
        //System.out.println(String.format("jdbc url: %s, username: %s, password: %s", bundle.getString("jdbc.url"), bundle.getString("jdbc.username"), bundle.getString("jdbc.password")));
        return DriverManager.getConnection(bundle.getString("jdbc.url"),
                bundle.getString("jdbc.username"),
                bundle.getString("jdbc.password")
        );
    }

    protected JdbcConnectionProvider getConnectionProvider() {
        return new JdbcConnectionProvider() {
            private Connection providedConnection = null;
            @Override
            public Connection getConnection() throws SQLException {
                if (providedConnection == null) {
                    providedConnection = getConnectionFromDriverManager();
                    providedConnection.setAutoCommit(false);
                }
                return providedConnection;
            }

            @Override
            public void closeConnection(Connection connection) throws SQLException {
                if (connection != null) {
                    connection.close();
                }
                this.providedConnection = null;
            }

            @Override
            public void startTransaction() {
                try {
                    providedConnection.setAutoCommit(false);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void commit() {
                try {
                    providedConnection.commit();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void rollback() {
                try {
                    providedConnection.rollback();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    protected String getProperty(String key) {
        return bundle.getString(key);
    }

    protected AFTransactionManager getAtomfeedTransactionManager(JdbcConnectionProvider connectionProvider) {
        return new AtomFeedJdbcTransactionManager(connectionProvider);
    }
}