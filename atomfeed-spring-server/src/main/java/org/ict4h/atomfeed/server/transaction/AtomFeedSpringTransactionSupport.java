package org.ict4h.atomfeed.server.transaction;

import org.apache.log4j.Logger;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.transaction.AFTransactionManager;
import org.ict4h.atomfeed.transaction.AFTransactionWork;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AtomFeedSpringTransactionSupport implements AFTransactionManager, JdbcConnectionProvider {
    private static Logger logger = Logger.getLogger(AtomFeedSpringTransactionSupport.class);
    private final DataSource dataSource;
    private PlatformTransactionManager transactionManager;
    private Map<AFTransactionWork.PropagationDefinition, Integer> propagationMap = new HashMap<AFTransactionWork.PropagationDefinition, Integer>();

    public AtomFeedSpringTransactionSupport(PlatformTransactionManager transactionManager, DataSource dataSource) {
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
        propagationMap.put(AFTransactionWork.PropagationDefinition.PROPAGATION_REQUIRED, TransactionDefinition.PROPAGATION_REQUIRED);
        propagationMap.put(AFTransactionWork.PropagationDefinition.PROPAGATION_REQUIRES_NEW, TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Override
    public <T> T executeWithTransaction(final AFTransactionWork<T> action) throws RuntimeException {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        Integer txPropagationDef = getTxPropagation(action.getTxPropagationDefinition());
        transactionTemplate.setPropagationBehavior(txPropagationDef);
        return transactionTemplate.execute( new TransactionCallback<T>() {
            @Override
            public T doInTransaction(TransactionStatus status) {
                return action.execute();
            }
        });
    }

    private Integer getTxPropagation(AFTransactionWork.PropagationDefinition propagationDefinition) {
        return propagationMap.get(propagationDefinition);
    }

    /**
     * @see org.ict4h.atomfeed.jdbc.JdbcConnectionProvider
     * @return
     * @throws java.sql.SQLException
     */
    @Override
    public Connection getConnection() throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }


    /**
     * @see org.ict4h.atomfeed.jdbc.JdbcConnectionProvider
     * @param connection
     * @throws java.sql.SQLException
     */
    @Override
    public void closeConnection(Connection connection) throws SQLException {
        logger.error("Close connection has been called on JdbcConnectionProvider. This should not happen");
    }


    /**
     *  @see org.ict4h.atomfeed.jdbc.JdbcConnectionProvider
     */
    @Override
    public void startTransaction() {
        logger.error("Start transaction has been called on JdbcConnectionProvider. This should not happen");
    }

    /**
     *  @see org.ict4h.atomfeed.jdbc.JdbcConnectionProvider
     */
    @Override
    public void commit() {
        logger.error("Commit has been called on JdbcConnectionProvider. This should not happen");
    }

    /**
     *  @see org.ict4h.atomfeed.jdbc.JdbcConnectionProvider
     */
    @Override
    public void rollback() {
        logger.error("Rollback has been called on JdbcConnectionProvider. This should not happen");
    }

}
