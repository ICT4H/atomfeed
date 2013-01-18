package org.ict4htw.atomfeed.server.feed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChunkingHistory {
	
	public class Range {
		public final Integer first;
		public final Integer last;
		public Range(Integer first, Integer last) {
			this.first = first;
			this.last = last;
		}
		
		public boolean isValid() {
			return this.last != Integer.MAX_VALUE;
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

		public int getFeedCount(int upperBound) {
			int endPosition = isOpen() ? upperBound : endPos;
			int count = ((Math.min(endPosition, upperBound) - startPos) + 1);
			if (count <= 0) return 0; 
			Double feedCnt = Math.ceil((count*1.0)/chunkSize);
			return feedCnt.intValue();
		}

		public Range getRange(Integer relativeFeedId) {
			int start = startPos + (relativeFeedId-1)*chunkSize;
			int naturalLimit = start + chunkSize - 1;
			int end   = isOpen() ? naturalLimit : Math.min(endPos,  naturalLimit);
			return new Range(start, end);
		}

		public boolean isOpen() {
			return (endPos == UNBOUNDED);
		}
	}
	
	List<Entry> entries = new ArrayList<Entry>();
	
	public void add(int seqNum, int chunkSize, int startPosition) {
		closeOffCurrent(startPosition-1);		
		Entry entry = new Entry(seqNum, chunkSize, startPosition);
		entries.add(entry);
		Collections.sort(entries, new Comparator<Entry>() {
			public int compare(Entry e1, Entry e2) {
				return (e1.seqNum > e2.seqNum) ? 1 : (e1.seqNum == e2.seqNum ? 0 : -1);
			}
		});
	}
	
	public Range findRange(Integer feedId, int upperBound) { 
		int feedsPassed = 0;
		for (Entry entry : entries) {
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
		for (Entry entry : entries) {
			feedCnt += entry.getFeedCount(limit); 
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
