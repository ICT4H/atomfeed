package org.ict4htw.atomfeed.client;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import org.ict4htw.atomfeed.IntegrationTest;
import org.ict4htw.atomfeed.client.repository.AllFeeds;
import org.ict4htw.atomfeed.client.repository.datasource.WebClient;
import org.ict4htw.atomfeed.server.repository.DbEventRecordCreator;
import org.ict4htw.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;

public class AtomFeedClientIT extends IntegrationTest {

    private DbEventRecordCreator recordCreator;
    private Connection connection;
    private AllEventRecordsJdbcImpl eventRecords;
    private AllFeeds allFeeds;

    @Before
    public void before() throws SQLException {
        connection = getConnection();
        Statement statement = connection.createStatement();
        statement.execute("TRUNCATE atomfeed.event_records  RESTART IDENTITY;");
        statement.close();
        eventRecords = new AllEventRecordsJdbcImpl(getProvider(connection));
        recordCreator = new DbEventRecordCreator(eventRecords);
        allFeeds = new AllFeeds(new WebClient());
    }

    @After
    public void after() throws SQLException {
        connection.close();
    }

    @Test
    public void shouldReadEventsCreatedEvents() throws URISyntaxException, SQLException {
        createOneEvent("One Event", "http://google.com");
        Feed feed = allFeeds.getFor(new URI(String.format("http://localhost:8080/events/1")));
        List entries = feed.getEntries();
        assertEquals(1, entries.size());
        Entry entry = (Entry) entries.get(0);
        assertEquals("One Event",entry.getTitle());
    }

    private void createOneEvent(String title, String url) throws URISyntaxException, SQLException {
        recordCreator.create(UUID.randomUUID().toString(),title,url,null);
        connection.commit();
    }
}
