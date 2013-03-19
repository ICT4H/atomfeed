package org.ict4h.atomfeed.server.repository;

import org.ict4h.atomfeed.IntegrationTest;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.timebasedchunkingconfiguration.TimeRange;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.junit.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


public class AllEventRecordsIT extends IntegrationTest {

    private AllEventRecords allEventRecords;
    private Connection connection;

    @Before
    public void purgeEventRecords() throws SQLException {
        connection = getConnection();
        allEventRecords = new AllEventRecordsJdbcImpl(getProvider(connection));
        Statement statement = connection.createStatement();
        statement.execute("delete from atomfeed.event_records");
        statement.close();
    }

    @After
    public void after() throws SQLException {
          connection.close();
    }

    @Test
    public void shouldAddEventRecordAndFetchByUUID() throws URISyntaxException {
    	System.out.println("executing shouldAddEventRecordAndFetchByUUID");
        String uuid = UUID.randomUUID().toString();
        EventRecord eventRecordAdded = new EventRecord(uuid, "title", new URI("http://uri"), null,new Date());
        allEventRecords.add(eventRecordAdded);
        EventRecord eventRecordFetched = allEventRecords.get(uuid);
        assertEquals(eventRecordAdded.getUuid(), eventRecordFetched.getUuid());
        assertEquals(eventRecordAdded.getTitle(), eventRecordFetched.getTitle());
        assertTrue((new Date()).after(eventRecordFetched.getTimeStamp()));
    }

    @Test
    public void shouldGetTotalCountOfEventRecords() throws URISyntaxException {
    	System.out.println("executing shouldGetTotalCountOfEventRecords");
        EventRecord eventRecordAdded1 = new EventRecord("uuid1", "title", new URI("http://uri"), null,new Date());
        EventRecord eventRecordAdded2 = new EventRecord("uuid2", "title", new URI("http://uri"), null,new Date());

        allEventRecords.add(eventRecordAdded1);
        allEventRecords.add(eventRecordAdded2);

        int totalCount = allEventRecords.getTotalCount();
        Assert.assertEquals(2, totalCount);
    }

    @Test 
    //relies on the rdbms serial id
    public void shouldGetEventsFromStartNumber() throws URISyntaxException {
    	System.out.println("executing shouldGetEventsFromStartNumber");
    	addEvents(6, "uuid");
        EventRecord e2 = allEventRecords.get("uuid2");
        EventRecord e5 = allEventRecords.get("uuid5");

        List<EventRecord> events = allEventRecords.getEventsFromRange(e2.getId(),e5.getId() );

        assertEquals(4, events.size());
        assertEquals(e2.getUuid(), events.get(0).getUuid());
        assertEquals(e5.getUuid(), events.get(events.size()-1).getUuid());
    }

    @Test
    public void shouldFindEventsInTimeRange() throws URISyntaxException, InterruptedException {
        Timestamp startTime = new Timestamp(new Date().getTime());
        addEvents(6, "uuid");
        Timestamp endTime = new Timestamp(new Date().getTime());
        List<EventRecord> events = allEventRecords.getEventsFromTimeRange(new TimeRange(startTime,endTime));
        EventRecord firstEvent = events.get(0);
        EventRecord lastEvent = events.get(5);
        assertEquals("Event1",firstEvent.getTitle());
        assertEquals("Event6",lastEvent.getTitle());
    }

    private void addEvents(int eventNumber, String uuidStartsWith) throws URISyntaxException {
        for (int i= 1; i <= eventNumber; i++) {
            String title = "Event" + i;
            allEventRecords.add(new EventRecord(uuidStartsWith + i, title, new URI("http://uri/"+title), null,new Date()));
        }
    }
}
