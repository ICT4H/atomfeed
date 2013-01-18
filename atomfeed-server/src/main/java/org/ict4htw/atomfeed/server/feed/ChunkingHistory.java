package org.ict4htw.atomfeed.server.feed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChunkingHistory {
	
	List<ChunkingEntry> entries = new ArrayList<ChunkingEntry>();
	
	public void add(int seqNum, int chunkSize, int startPosition) {
		closeOffCurrent(startPosition-1);		
		ChunkingEntry entry = new ChunkingEntry(seqNum, chunkSize, startPosition);
		entries.add(entry);
		Collections.sort(entries, new Comparator<ChunkingEntry>() {
			public int compare(ChunkingEntry e1, ChunkingEntry e2) {
				return (e1.getSeqNum() > e2.getSeqNum()) ? 1 : (e1.getSeqNum() == e2.getSeqNum() ? 0 : -1);
			}
		});
	}
	
	public ChunkingEntry.Range findRange(Integer feedId, int upperBound) { 
		int feedsPassed = 0;
		for (ChunkingEntry entry : entries) {
			int feedCount = entry.getFeedCount(upperBound);
			if (entry.isOpen() || (feedsPassed + feedCount >= feedId)) {
				return entry.getRange(feedId - feedsPassed);
			} else {
				feedsPassed += feedCount;
			}
		}		
		return null;
	}
	
	public int getNumberOfFeeds(int limit) {
		int feedCnt = 0;
		for (ChunkingEntry entry : entries) {
			feedCnt += entry.getFeedCount(limit); 
		}
		return feedCnt;
	}

	private void closeOffCurrent(int endPosition) {
		ChunkingEntry current = getCurrentEntry();
		if (current != null) {
			current.close(endPosition);
		}
	}

	private ChunkingEntry getCurrentEntry() {
		int size = entries.size();
		return (size > 0) ? entries.get(entries.size()-1) : null;
	}

}
