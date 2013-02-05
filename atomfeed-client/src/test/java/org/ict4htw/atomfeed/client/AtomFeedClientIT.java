package org.ict4htw.atomfeed.client;

import junit.framework.Assert;
import org.ict4htw.atomfeed.SpringIntegrationIT;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.repository.DbEventRecordCreator;
import org.ict4htw.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class AtomFeedClientIT extends SpringIntegrationIT{

    private DbEventRecordCreator recordCreator;
    private Connection connection;
    private AllEventRecordsJdbcImpl eventRecords;

    @Before
    public void before() throws SQLException {
        connection = getConnection();
        Statement statement = connection.createStatement();
        statement.execute("delete from atomfeed.event_records");
        statement.close();
        eventRecords = new AllEventRecordsJdbcImpl(getProvider(connection));
        recordCreator = new DbEventRecordCreator(eventRecords);
    }

    @After
    public void after() throws SQLException {
        connection.close();
    }

    @Test
    public void shouldReadEventsCreatedEvents() throws URISyntaxException {
        String uuid = UUID.randomUUID().toString();
        createOneEvent(uuid,"One Event","http://google.com");
        EventRecord eventRecord = eventRecords.get(uuid);
        Assert.assertNotNull(eventRecord);
    }

    private void createOneEvent(String uuid,String title, String url) throws URISyntaxException {
        recordCreator.create(uuid,title,url,null);
    }
}
