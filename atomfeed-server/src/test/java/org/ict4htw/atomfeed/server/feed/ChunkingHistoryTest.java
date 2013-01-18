package org.ict4htw.atomfeed.server.feed;

import junit.framework.Assert;
import org.junit.Test;

public class ChunkingHistoryTest {
	
	@Test
	public void shouldGetFeedCountGivenTotalNumRecordsFromSingleHistory() {
		ChunkingHistory history = getSingleEntryHistory();
		Assert.assertEquals(3, history.getNumberOfFeeds(12));
		Assert.assertEquals(2, history.getNumberOfFeeds(10));
		Assert.assertEquals(0, history.getNumberOfFeeds(0));
	}
	
	@Test
	public void shouldGetFeedCountGivenTotalNumRecordsFromMultipleHistory() {
		ChunkingHistory history = getMultiEntryHistory();
		Assert.assertEquals(4, history.getNumberOfFeeds(18));
		Assert.assertEquals(5, history.getNumberOfFeeds(21));
		Assert.assertEquals(3, history.getNumberOfFeeds(13));
		Assert.assertEquals(1, history.getNumberOfFeeds(1));
	}
	
	@Test
	public void shouldFindRangeForAGivenFeedWithSingleHistory() {
		ChunkingHistory history = getSingleEntryHistory();
		assertRange(6, 10, history.findRange(2, 11));
		assertRange(1, 5, history.findRange(1, 11));
	}
	
	@Test
	public void shouldFindRangeForAGivenFeedWithMultiHistory() {
		ChunkingHistory history = getMultiEntryHistory();		
		assertRange(6, 10, history.findRange(2, 11));	
		assertRange(11, 13, history.findRange(3, 11));	
		assertRange(11, 13, history.findRange(3, 100));		
		assertRange(14, 20, history.findRange(4, 100));
	}

	private void assertRange(int first, int last, ChunkingEntry.Range range) {
		Assert.assertEquals(first, range.first.intValue());
		Assert.assertEquals(last, range.last.intValue());
	}
	
	private ChunkingHistory getSingleEntryHistory() {
		ChunkingHistory history = new ChunkingHistory();
		history.add(1, 5, 1);
		return history;
	}

	private ChunkingHistory getMultiEntryHistory() {
		ChunkingHistory history = new ChunkingHistory();
		history.add(1, 5, 1);
		history.add(2, 7, 14);
		return history;
	}

}

