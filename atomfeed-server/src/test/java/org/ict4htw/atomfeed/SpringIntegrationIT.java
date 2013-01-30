package org.ict4htw.atomfeed;

import org.ict4htw.atomfeed.server.repository.hibernate.DataAccessTemplate;
import org.ict4htw.atomfeed.server.repository.jdbc.JdbcConnectionProvider;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:atomFeedContext.xml")
@Transactional
public abstract class SpringIntegrationIT {
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