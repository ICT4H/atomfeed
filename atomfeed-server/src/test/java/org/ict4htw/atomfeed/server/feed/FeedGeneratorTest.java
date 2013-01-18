package org.ict4htw.atomfeed.server.feed;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import junit.framework.Assert;

import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.ict4htw.atomfeed.server.repository.AllEventRecordsStub;
import org.junit.Before;
import org.junit.Test;


public class FeedGeneratorTest {
	AllEventRecords eventsRecord = new AllEventRecordsStub();
	private ChunkingHistory config;
	private FeedGenerator feedGenerator;
	
	@Before
	public void setUp() {
		config = new ChunkingHistory();
		config.add(1, 5, 1);
		feedGenerator = new FeedGenerator(eventsRecord, config);
	}
	
	@Test(expected=Exception.class)
	public void shouldErrorOutForMissingId() {
		EventFeed feed = feedGenerator.getFeedForId(null);
	}
	
	@Test(expected=Exception.class)
	public void shouldErrorOutForInvalidId() {
		EventFeed feed = feedGenerator.getFeedForId(0);
	}
	
	@Test(expected=Exception.class)
	public void shouldErrorOutForFutureFeed() {
		EventFeed feed = feedGenerator.getFeedForId(999);
	}
	
	@Test
	public void shouldRetrieveGivenFeed() throws Exception {
		addEvents(11);
		EventFeed feed = feedGenerator.getFeedForId(1);
		Assert.assertEquals(1, feed.getId().intValue());
		Assert.assertEquals(5, feed.getEvents().size());
	}
	
	@Test
	public void shouldRetrieveRecentFeed() throws Exception {
		addEvents(15);
		EventFeed feed = feedGenerator.getRecentFeed();
		Assert.assertEquals(3, feed.getId().intValue());
		Assert.assertEquals(5, feed.getEvents().size());
	}
	
	private void addEvents(int eventNumber) throws URISyntaxException {
		for (int i= 1; i <= eventNumber; i++) {
			String title = "Event" + i;
			eventsRecord.add(new EventRecord(UUID.randomUUID().toString(), title, new URI("http://uri/"+title), null));
		}
	}
	
	
}
