package org.ict4htw.atomfeed.server.repository;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.ict4htw.atomfeed.SpringIntegrationIT;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;


public class AllEventRecordsIT extends SpringIntegrationIT {

    @Autowired
    private AllEventRecords allEventRecords;
    
    private JdbcTemplate jdbcTemplate;

	@Autowired 
	private void setDataSource(DataSource ds) {
		jdbcTemplate = new JdbcTemplate(ds);
	}

	@Before
	@org.junit.After
    public void purgeEventRecords() {
		int queryForInt = jdbcTemplate.queryForInt("select count(*) from atomfeed.event_records");
		System.out.println("Total records " + queryForInt);
		int update = jdbcTemplate.update("delete from atomfeed.event_records");
    	System.out.println("result of delete=" + update);
    	//template.deleteAll(template.loadAll(EventRecord.class));
    }

    @Test
    @Transactional
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

  //@Ignore(value = "Transaction Snafu after Transaction handling was moved to SpringIT.")
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

    private void addEvents(int eventNumber, String uuidStartsWith) throws URISyntaxException {
    	Calendar c = Calendar.getInstance();
    	c.add(Calendar.SECOND, 10);
        for (int i= 1; i <= eventNumber; i++) {
            String title = "Event" + i;
            allEventRecords.add(new EventRecord(uuidStartsWith + i, title, new URI("http://uri/"+title), null,new Date()));
        }
    }
}
