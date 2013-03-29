package org.ict4h.atomfeed.server.domain.chunking.number;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NumberChunkingHistory {
	List<NumberChunkingHistoryEntry> entryNumberBasedHistories = new ArrayList<NumberChunkingHistoryEntry>();
	
	public void add(int seqNum, int chunkSize, int startPosition) {
		closeOffCurrent(startPosition-1);		
		NumberChunkingHistoryEntry entryNumberBasedHistory = new NumberChunkingHistoryEntry(seqNum, chunkSize, startPosition);
		entryNumberBasedHistories.add(entryNumberBasedHistory);
		Collections.sort(entryNumberBasedHistories, new Comparator<NumberChunkingHistoryEntry>() {
			public int compare(NumberChunkingHistoryEntry e1, NumberChunkingHistoryEntry e2) {
				return (e1.getSequenceNumber() > e2.getSequenceNumber()) ? 1 : (e1.getSequenceNumber() == e2.getSequenceNumber() ? 0 : -1);
			}
		});
	}
	
	public NumberRange findRange(Integer feedId, int upperBound) {
		int feedsPassed = 0;
		for (NumberChunkingHistoryEntry entryNumberBasedHistory : entryNumberBasedHistories) {
			int feedCount = entryNumberBasedHistory.getFeedCount(upperBound);
			if (entryNumberBasedHistory.isOpen() || (feedsPassed + feedCount >= feedId)) {
				return entryNumberBasedHistory.getRange(feedId - feedsPassed);
			} else {
				feedsPassed += feedCount;
			}
		}		
		return null;
	}
	
	public int getNumberOfFeeds(int limit) {
		int feedCount = 0;
		for (NumberChunkingHistoryEntry entryNumberBasedHistory : entryNumberBasedHistories) {
			feedCount += entryNumberBasedHistory.getFeedCount(limit);
		}
		return feedCount;
	}

	private void closeOffCurrent(int endPosition) {
		NumberChunkingHistoryEntry current = getCurrentEntry();
		if (current != null) {
			current.close(endPosition);
		}
	}

	private NumberChunkingHistoryEntry getCurrentEntry() {
		int size = entryNumberBasedHistories.size();
		return (size > 0) ? entryNumberBasedHistories.get(entryNumberBasedHistories.size()-1) : null;
	}

}
