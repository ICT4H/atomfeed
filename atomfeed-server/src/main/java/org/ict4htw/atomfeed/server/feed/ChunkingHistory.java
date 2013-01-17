package org.ict4htw.atomfeed.server.feed;

import java.util.ArrayList;
import java.util.List;

public class ChunkingHistory {
	
	public class Range {
		public final Integer first;
		public final Integer last;
		public Range(Integer first, Integer last) {
			this.first = first;
			this.last = last;
		}
	}

	private static final int UNBOUNDED = -1;
	
	private class Entry {
		private int seqNum;
		private int chunkSize;
		private int endPos;
		private int startPos;
		
		public Entry(int seqNum, int chunkSize, Integer startPos) {
			this.seqNum = seqNum;
			this.chunkSize = chunkSize;
			this.startPos = (startPos == null) ? 1 : startPos;
			this.endPos = UNBOUNDED;
		}

		public int getFeedCount(int recCount) {
			int endPosition = (endPos == UNBOUNDED) ? recCount : endPos;
			int count = ((Math.min(endPosition, recCount) - startPos) + 1);
			if (count <= 0) return 0; 
			Double feedCnt = Math.ceil((count*1.0)/chunkSize);
			return feedCnt.intValue();
		}

		public Range getRange(Integer relativeFeedId, Integer recCount) {
			int start = startPos + (relativeFeedId-1)*chunkSize;
			int naturalLimit = start + chunkSize - 1;
			int end   = (endPos == UNBOUNDED) ? naturalLimit : Math.min(Math.min(endPos, recCount),  naturalLimit);
			return new Range(start, end);
		}
	}
	
	List<Entry> entries = new ArrayList<Entry>();
	
	public void add(int seqNum, int chunkSize, int startPosition) {
		closeOffCurrent(startPosition-1);		
		Entry entry = new Entry(seqNum, chunkSize, startPosition);
		entries.add(entry);
	}
	
	public Range findRange(Integer feedId, int recCount) {
		int feedsPassed = 0;
		for (Entry entry : entries) {
			int entryFeedCount = entry.getFeedCount(recCount);
			feedsPassed += entryFeedCount;
			if (feedsPassed >= feedId) {
				return entry.getRange(feedId - (feedsPassed - entryFeedCount), recCount);
			}
		}		
		return null;
	}
	
	public int getNumberOfFeeds(int recCount) {
		int feedCnt = 0;
		for (Entry entry : entries) {
			feedCnt += entry.getFeedCount(recCount); 
		}
		return feedCnt;
	}

	private void closeOffCurrent(int endPosition) {
		Entry current = getCurrentEntry();
		if (current != null) {
			current.endPos = endPosition;
		}
	}

	private Entry getCurrentEntry() {
		int size = entries.size();
		return (size > 0) ? entries.get(entries.size()-1) : null;
	}

}
