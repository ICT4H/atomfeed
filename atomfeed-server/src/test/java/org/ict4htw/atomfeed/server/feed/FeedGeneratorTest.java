package org.ict4htw.atomfeed.server.feed;

import junit.framework.Assert;

import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.ict4htw.atomfeed.server.repository.AllEventRecordsStub;
import org.junit.Before;
import org.junit.Test;

import com.sun.syndication.feed.atom.Feed;


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
		EventFeed feed = feedGenerator.getFeedForId("");
	}
	
	@Test(expected=Exception.class)
	public void shouldErrorOutForInvalidId() {
		EventFeed feed = feedGenerator.getFeedForId("1a");
	}
	
	@Test
	public void shouldRetrieveGivenFeed() {
		EventFeed feed = feedGenerator.getFeedForId("1");
		Assert.assertEquals("1", feed.getId());
		Assert.assertEquals(5, feed.getEvents().size());
		
	}
	
	
}
