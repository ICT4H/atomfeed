package org.ict4htw.atomfeed.server.feed;

import junit.framework.Assert;

import org.junit.Test;

public class ChunkingHistoryTest {
	
	@Test
	public void shouldGetFeedCountGivenTotalNumRecordsFromSingleHistory() {
		ChunkingHistory history = getSingleEntryHisotry();
		Assert.assertEquals(3, history.getNumberOfFeeds(12));
		Assert.assertEquals(2, history.getNumberOfFeeds(10));
		Assert.assertEquals(0, history.getNumberOfFeeds(0));
	}
	
	@Test
	public void shouldGetFeedCountGivenTotalNumRecordsFromMultipleHistory() {
		ChunkingHistory history = getMultiEntryHisotry();
		Assert.assertEquals(4, history.getNumberOfFeeds(18));
		Assert.assertEquals(5, history.getNumberOfFeeds(21));
		Assert.assertEquals(3, history.getNumberOfFeeds(13));
		Assert.assertEquals(1, history.getNumberOfFeeds(1));
	}
	
	@Test
	public void shouldFindRangeForAGivenFeedWithSingleHistory() {
		ChunkingHistory history = getSingleEntryHisotry();
		ChunkingHistory.Range range2 = history.findRange(2, 11);
		Assert.assertEquals(6, range2.first.intValue());
		Assert.assertEquals(10, range2.last.intValue());
		
		ChunkingHistory.Range range1 = history.findRange(1, 11);
		Assert.assertEquals(1, range1.first.intValue());
		Assert.assertEquals(5, range1.last.intValue());
	}
	
	@Test
	public void shouldFindRangeForAGivenFeedWithMultiHistory() {
		ChunkingHistory history = getMultiEntryHisotry();
		ChunkingHistory.Range range2 = history.findRange(2, 11);
		Assert.assertEquals(6, range2.first.intValue());
		Assert.assertEquals(10, range2.last.intValue());
		
		ChunkingHistory.Range range3 = history.findRange(3, 11);
		Assert.assertEquals(11, range3.first.intValue());
		Assert.assertEquals(11, range3.last.intValue());
		
		ChunkingHistory.Range range3a = history.findRange(3, 100);
		Assert.assertEquals(11, range3a.first.intValue());
		Assert.assertEquals(13, range3a.last.intValue());
		
		ChunkingHistory.Range range4 = history.findRange(4, 100);
		Assert.assertEquals(14, range4.first.intValue());
		Assert.assertEquals(20, range4.last.intValue());
	}
	
	
	
	private ChunkingHistory getSingleEntryHisotry() {
		ChunkingHistory history = new ChunkingHistory();
		history.add(1, 5, 1);
		return history;
	}

	private ChunkingHistory getMultiEntryHisotry() {
		ChunkingHistory history = new ChunkingHistory();
		history.add(1, 5, 1);
		history.add(2, 7, 14);
		return history;
	}

}
