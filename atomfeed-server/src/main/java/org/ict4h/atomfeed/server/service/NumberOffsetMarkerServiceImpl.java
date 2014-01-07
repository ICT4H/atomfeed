package org.ict4h.atomfeed.server.service;

import org.ict4h.atomfeed.server.domain.EventFeed;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.chunking.ChunkingHistoryEntry;
import org.ict4h.atomfeed.server.domain.chunking.number.NumberChunkingHistory;
import org.ict4h.atomfeed.server.domain.chunking.number.NumberRange;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;
import org.ict4h.atomfeed.server.repository.EventRecordsOffsetMarkers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NumberOffsetMarkerServiceImpl implements OffsetMarkerService {

    private AllEventRecords allEventRecords;
    private EventRecordsOffsetMarkers eventRecordsOffsetMarkers;
    private NumberChunkingHistory chunkingHistory;

    public NumberOffsetMarkerServiceImpl(AllEventRecords allEventRecords, ChunkingEntries chunkingEntries, EventRecordsOffsetMarkers eventRecordsOffsetMarkers) {
        this.allEventRecords = allEventRecords;
        this.eventRecordsOffsetMarkers = eventRecordsOffsetMarkers;
        this.chunkingHistory = getChunkingHistory(chunkingEntries);
    }

    @Override
    public void markEvents(Integer offsetBy) {
        List<String> categories = this.allEventRecords.findCategories();
        categories.add(""); // marker for all categories
        markEvents(categories.toArray(new String[categories.size()]), offsetBy);
    }

    @Override
    public void markEvents(String[] categories, Integer offsetBy) {
        for (String category : categories) {
            offsetEventsForCategory(category, offsetBy);
        }
    }

    private void offsetEventsForCategory(String category, int markerOffset) {
        int countForCategory = allEventRecords.getTotalCountForCategory(category);
        int latestFeedId = chunkingHistory.getNumberOfFeeds(countForCategory);
        if(latestFeedId == 0) {
            return;
        }
        int markedEventsCount = (countForCategory - markerOffset);
        if (markedEventsCount <= 0) {
            return;
        }

        int markedFeedId = chunkingHistory.getNumberOfFeeds(markedEventsCount);
        EventFeed feed = findFeed(markedFeedId, category, markedEventsCount);
        List<EventRecord> events = feed.getEvents();
        Collections.sort(events, new Comparator<EventRecord>() {
            @Override
            public int compare(EventRecord e1, EventRecord e2) {
                return (e1.getId() > e2.getId() ? -1 : (e1.getId().equals(e2.getId()) ? 0 : 1));
            }
        });

        if (events.isEmpty()) {
            return;
        }
        EventRecord lastEventInFeed = events.get(0);
        int totalCountForCategory = allEventRecords.getTotalCountForCategory(category, null, lastEventInFeed.getId());
        eventRecordsOffsetMarkers.setOffSetMarkerForCategory(category, lastEventInFeed.getId(), totalCountForCategory);
    }

    private EventFeed findFeed(int feedId, String category, int totalCountForCategory) {
        NumberRange feedRange = chunkingHistory.findRange(feedId, totalCountForCategory);
        List<EventRecord> events = allEventRecords.getEventsFromRangeForCategory(category, feedRange.getOffset(), feedRange.getLimit(), 0);
        return new EventFeed(feedId, events);
    }

    private NumberChunkingHistory getChunkingHistory(ChunkingEntries chunkingEntries) {
        NumberChunkingHistory numberBasedChunking = new NumberChunkingHistory();
        List<ChunkingHistoryEntry> allEntries = chunkingEntries.all();
        for (ChunkingHistoryEntry entry : allEntries) {
            numberBasedChunking.add(entry.getSequenceNumber(), entry.getInterval().intValue(), entry.getLeftBound().intValue());
        }
        return numberBasedChunking;
    }
}
