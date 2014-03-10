package org.ict4h.atomfeed.jdbc;

import org.ict4h.atomfeed.transaction.AFTransactionManager;
import org.ict4h.atomfeed.transaction.AFTransactionWork;

import java.sql.Connection;
import java.sql.SQLException;

public class AtomFeedJdbcTransactionManager implements AFTransactionManager {
    private JdbcConnectionProvider connectionProvider;

    public AtomFeedJdbcTransactionManager(JdbcConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public <T> T executeWithTransaction(AFTransactionWork<T> action) throws RuntimeException {
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            connection.setAutoCommit(false);
            T result = action.execute();
            connection.commit();
            connectionProvider.closeConnection(connection);
            return result;
        } catch (SQLException e ) {
            if (connection != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    connection.rollback();
                } catch(SQLException excep) {
                    throw new RuntimeException("Error occurred while trying to rollback transaction", excep);
                }
            }
            throw new RuntimeException("Error occurred while trying to execute in transaction", e);
        } finally {
            //connection.setAutoCommit(true);
        }
    }
}
