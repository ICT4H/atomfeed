package org.openmrs.module.feedpublishermodule.repository.jdbc;

import org.hibernate.SessionFactory;
import org.hibernate.connection.C3P0ConnectionProvider;
import org.hibernate.engine.SessionFactoryImplementor;
import org.ict4htw.atomfeed.server.repository.jdbc.JdbcConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.SQLException;

public class OpenMRSConnectionProvider implements JdbcConnectionProvider {

    private SessionFactory sessionFactory;

    @Autowired
    public OpenMRSConnectionProvider(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    //Use the doWork Paradigm instead of getting a Connection like this.
    //Check if context is available at this point of time in the module life cycle.
    @Override
    public Connection getConnection() throws SQLException {
        SessionFactoryImplementor implementor = (SessionFactoryImplementor) sessionFactory;
        C3P0ConnectionProvider cp = (C3P0ConnectionProvider) implementor.getConnectionProvider();
        return cp.getConnection();
    }
}
