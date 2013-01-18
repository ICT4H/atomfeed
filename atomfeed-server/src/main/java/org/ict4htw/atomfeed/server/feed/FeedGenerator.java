package org.ict4htw.atomfeed.server.feed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.feed.ChunkingHistory.Range;

public class FeedGenerator {

	private AllEventRecords eventsRecord;
	private ChunkingHistory history;
	
	public FeedGenerator(AllEventRecords eventsRecord, ChunkingHistory history) {
		this.eventsRecord = eventsRecord;
		this.history = history;
	}

	public EventFeed getFeedForId(String feedId) {
		validateFeedId(feedId);
		Range feedRange = getFeedRange(feedId);
		List<EventRecord> events = eventsRecord.getEventsFromRange(feedRange.first, feedRange.last);
		return new EventFeed(feedId, events);
	}

	private Range getFeedRange(String feedId) {
		return history.findRange(Integer.valueOf(feedId), eventsRecord.getTotalCount());
	}

	private void validateFeedId(String feedId) {
		if ( (feedId == null) || ("".equals(feedId.trim()) )) {
			throw new RuntimeException("feedId must not be null or emptry String");
		}
		Integer.valueOf(feedId);
		//should we validate that feedId > 0?
	}
	
	public static void main(String[] args) {
//		List<Integer> list = new ArrayList<Integer>();
//        list.add(5);
//        list.add(4);
//        list.add(3);
//        list.add(7);
//        list.add(2);
//        list.add(1);
//        Collections.sort(list, new Comparator<Integer> () {
//			public int compare(Integer e1, Integer e2) {
//				return (e1 > e2) ? 1 : (e1 == e2 ? 0 : -1);
//			}
//        });
//        for (Integer integer : list) {
//            System.out.println(integer);
//        }
		int i = Integer.MAX_VALUE;
		System.out.println(Integer.valueOf("2147483647"));
		if ((i + 2) > Integer.MAX_VALUE) System.out.println("infinite");
		System.out.println(i);
		System.out.println(Math.abs(-11));
	}

}
