package org.ict4h.atomfeed.server.domain.chunking.number;

import junit.framework.Assert;
import org.junit.Test;

public class NumberChunkingHistoryTest {
	@Test
	public void shouldGetFeedCountGivenTotalNumRecordsFromSingleHistory() {
		NumberChunkingHistory historyNumberBased = getSingleEntryHistory();
		Assert.assertEquals(3, historyNumberBased.getNumberOfFeeds(12));
		Assert.assertEquals(2, historyNumberBased.getNumberOfFeeds(10));
		Assert.assertEquals(0, historyNumberBased.getNumberOfFeeds(0));
	}
	
	@Test
	public void shouldGetFeedCountGivenTotalNumRecordsFromMultipleHistory() {
		NumberChunkingHistory historyNumberBased = getMultiEntryHistory();
		Assert.assertEquals(4, historyNumberBased.getNumberOfFeeds(18));
		Assert.assertEquals(5, historyNumberBased.getNumberOfFeeds(24));
		Assert.assertEquals(3, historyNumberBased.getNumberOfFeeds(13));
		Assert.assertEquals(1, historyNumberBased.getNumberOfFeeds(1));
	}
	
	@Test
	public void shouldFindRangeForAGivenFeedWithSingleHistory() {
		NumberChunkingHistory historyNumberBased = getSingleEntryHistory();
		assertRange(6, 5, historyNumberBased.findRange(2, 11));
		assertRange(1, 5, historyNumberBased.findRange(1, 11));
	}
	
	@Test
	public void shouldFindRangeForAGivenFeedWithMultiHistory() {
		NumberChunkingHistory historyNumberBased = getMultiEntryHistory();
		assertRange(6, 5, historyNumberBased.findRange(2, 11));
		assertRange(11, 5, historyNumberBased.findRange(3, 11));
		assertRange(11, 5, historyNumberBased.findRange(3, 100));
		assertRange(16, 7, historyNumberBased.findRange(4, 100));
		assertRange(23, 7, historyNumberBased.findRange(5, 100));
	}

	private void assertRange(int first, int last, NumberRange range) {
		Assert.assertEquals(first, range.offset.intValue());
		Assert.assertEquals(last, range.limit.intValue());
	}
	
	private NumberChunkingHistory getSingleEntryHistory() {
		NumberChunkingHistory historyNumberBased = new NumberChunkingHistory();
		historyNumberBased.add(1, 5, 1);
		return historyNumberBased;
	}

	private NumberChunkingHistory getMultiEntryHistory() {
		NumberChunkingHistory chunkingHistory = new NumberChunkingHistory();
		chunkingHistory.add(1, 5, 1);
		chunkingHistory.add(2, 7, 16);
		return chunkingHistory;
	}

}

