package org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NumberBasedChunkingHistory {
	List<NumberBasedChunkingHistoryEntry> entryNumberBasedHistories = new ArrayList<NumberBasedChunkingHistoryEntry>();
	
	public void add(int seqNum, int chunkSize, int startPosition) {
		closeOffCurrent(startPosition-1);		
		NumberBasedChunkingHistoryEntry entryNumberBasedHistory = new NumberBasedChunkingHistoryEntry(seqNum, chunkSize, startPosition);
		entryNumberBasedHistories.add(entryNumberBasedHistory);
		Collections.sort(entryNumberBasedHistories, new Comparator<NumberBasedChunkingHistoryEntry>() {
			public int compare(NumberBasedChunkingHistoryEntry e1, NumberBasedChunkingHistoryEntry e2) {
				return (e1.getSeqNum() > e2.getSeqNum()) ? 1 : (e1.getSeqNum() == e2.getSeqNum() ? 0 : -1);
			}
		});
	}
	
	public NumberRange findRange(Integer feedId, int upperBound) {
		int feedsPassed = 0;
		for (NumberBasedChunkingHistoryEntry entryNumberBasedHistory : entryNumberBasedHistories) {
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
		for (NumberBasedChunkingHistoryEntry entryNumberBasedHistory : entryNumberBasedHistories) {
			feedCount += entryNumberBasedHistory.getFeedCount(limit);
		}
		return feedCount;
	}

	private void closeOffCurrent(int endPosition) {
		NumberBasedChunkingHistoryEntry current = getCurrentEntry();
		if (current != null) {
			current.close(endPosition);
		}
	}

	private NumberBasedChunkingHistoryEntry getCurrentEntry() {
		int size = entryNumberBasedHistories.size();
		return (size > 0) ? entryNumberBasedHistories.get(entryNumberBasedHistories.size()-1) : null;
	}

}
