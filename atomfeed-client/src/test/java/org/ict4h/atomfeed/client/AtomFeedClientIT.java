package org.ict4h.atomfeed.client;

import org.ict4h.atomfeed.IntegrationTest;
import org.ict4h.atomfeed.client.api.AtomFeedClient;
import org.ict4h.atomfeed.client.api.data.Event;
import org.ict4h.atomfeed.client.factory.AtomClientFactory;
import org.ict4h.atomfeed.client.repository.datasource.MemoryMarkerDataSource;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.repository.DbEventRecordCreator;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;

public class AtomFeedClientIT extends IntegrationTest {

    private DbEventRecordCreator recordCreator;
    private Connection connection;
    private AllEventRecordsJdbcImpl eventRecords;
    private AtomFeedClient atomFeedClient;

    @Before
    public void before() throws SQLException {
        connection = getConnection();
        Statement statement = connection.createStatement();
        statement.execute("TRUNCATE atomfeed.event_records  RESTART IDENTITY;");
        statement.close();
        eventRecords = new AllEventRecordsJdbcImpl(getProvider(connection));
        recordCreator = new DbEventRecordCreator(eventRecords);
    }

    @After
    public void after() throws SQLException {
        connection.close();
    }

    @Ignore
    public void shouldReadEventsCreatedEvents() throws URISyntaxException, SQLException {
        List<EventRecord> eventRecords = createEvents(7, "Hello, DiscWorld");
        String uriTemplate = "http://localhost:8080/events/";
        atomFeedClient = new AtomClientFactory().create(new MemoryMarkerDataSource());

        atomFeedClient.processedTo(getURI(uriTemplate,2),eventRecords.get(5).getTagUri());
        List<Event> events = atomFeedClient.unprocessedEvents(getURI(uriTemplate, 2));
        assertEquals(2,events.size());
    }

    private URI getURI(String template,int feedId) throws URISyntaxException {
        return new URI(String.format("%s%s", template, feedId));
    }

    private List<EventRecord> createEvents(int numberOfEventsToCreate, String titleTemplate) throws URISyntaxException, SQLException {
        List<EventRecord> records = new ArrayList<EventRecord>();
        int index = 0;
        do
        {
            index++;
            records.add(createOneEvent(String.format("%s%s", titleTemplate, index), String.format("%s%s", "http://google.com?q=", index)));
        }while (index <= numberOfEventsToCreate);
        return records;
    }

    private EventRecord createOneEvent(String title, String url) throws URISyntaxException, SQLException {
        EventRecord eventRecord = recordCreator.create(UUID.randomUUID().toString(), title, url, null);
        connection.commit();
        return eventRecord;
    }
}
