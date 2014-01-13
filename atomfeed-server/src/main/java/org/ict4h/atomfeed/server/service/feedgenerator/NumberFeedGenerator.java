package org.ict4h.atomfeed.server.service.feedgenerator;

import org.ict4h.atomfeed.server.domain.EventFeed;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.EventRecordsOffsetMarker;
import org.ict4h.atomfeed.server.domain.chunking.ChunkingHistoryEntry;
import org.ict4h.atomfeed.server.domain.chunking.number.NumberChunkingHistory;
import org.ict4h.atomfeed.server.domain.chunking.number.NumberRange;
import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;
import org.ict4h.atomfeed.server.repository.AllEventRecordsOffsetMarkers;

import java.util.ArrayList;
import java.util.List;

public class NumberFeedGenerator implements FeedGenerator {
    private AllEventRecords allEventRecords;
    private AllEventRecordsOffsetMarkers allEventRecordsOffsetMarkers;
    private ChunkingEntries chunkingEntries;
    private NumberChunkingHistory numberChunkingHistory;
    private final Object lockObject = new Object();

    public NumberFeedGenerator(AllEventRecords eventsRecord, AllEventRecordsOffsetMarkers allEventRecordsOffsetMarkers, ChunkingEntries chunkingEntries) {
        this.allEventRecords = eventsRecord;
        this.allEventRecordsOffsetMarkers = allEventRecordsOffsetMarkers;
        this.chunkingEntries = chunkingEntries;
    }

    @Override
    public EventFeed getFeedForId(Integer feedId, String category) {
        EventRecordsOffsetMarker marker = findMarker(category);
        int totalCountForCategory = getTotalCountForCategory(category, marker);
        validateFeedId(feedId, totalCountForCategory);
        return findFeed(feedId, category, totalCountForCategory, marker);
    }

    @Override
    public EventFeed getRecentFeed(String category) {
        EventRecordsOffsetMarker marker = findMarker(category);
        int totalCountForCategory = getTotalCountForCategory(category, marker);
        int latestFeed = getNumberChunkingHistory().getNumberOfFeeds(totalCountForCategory);
        if (isFeedZeroWithoutAnyEvents(latestFeed)) {
            return new EventFeed(0, new ArrayList<EventRecord>());
        }
        return findFeed(latestFeed, category, totalCountForCategory, marker);
    }

    private int getTotalCountForCategory(String category, EventRecordsOffsetMarker marker) {
        Integer eventCountTillOffset = (marker != null) ? marker.getEventCount() : 0;
        Integer offsetEventId = (marker != null) ? marker.getEventId() : null;
        int totalCountBeyondOffset = allEventRecords.getTotalCountForCategory(category, offsetEventId, null);
        return eventCountTillOffset + totalCountBeyondOffset;
    }

    private EventRecordsOffsetMarker findMarker(String category) {
        List<EventRecordsOffsetMarker> markers = allEventRecordsOffsetMarkers.getAll();
        String markerCategory = category == null ? "" : category;
        for (EventRecordsOffsetMarker marker : markers) {
            if (markerCategory.equals(marker.getCategory())) {
                return marker;
            }
        }
        return null;
    }

    private boolean isFeedZeroWithoutAnyEvents(int latestFeed) {
        return latestFeed == 0;
    }

    private EventFeed findFeed(int feedId, String category, int totalCountForCategory, EventRecordsOffsetMarker marker) {
        NumberRange feedRange = getNumberChunkingHistory().findRange(feedId, totalCountForCategory);
        Integer startEventId = 0;
        int offset = feedRange.getOffset();
        if (marker != null) {
            int relativeOffset = feedRange.getOffset() - marker.getEventCount();
            if (relativeOffset > 0) {
                offset = relativeOffset;
                startEventId = marker.getEventId();
            }
        }
        List<EventRecord> events = allEventRecords.getEventsFromRangeForCategory(category, offset, feedRange.getLimit(), startEventId);
        return new EventFeed(feedId, events);
    }

    private void validateFeedId(Integer feedId, int totalCountForCategory) {
        if ((feedId == null) || (feedId <= 0)) {
            throw new AtomFeedRuntimeException("feedId must not be null and must be greater than 0");
        }
        int numberOfFeeds = getNumberChunkingHistory().getNumberOfFeeds(totalCountForCategory);
        if (feedId > numberOfFeeds) {
            throw new AtomFeedRuntimeException("feed does not exist");
        }
    }

    private NumberChunkingHistory getNumberChunkingHistory() {
        if (this.numberChunkingHistory == null) {
            synchronized (lockObject) {
                NumberChunkingHistory numberBasedChunking = new NumberChunkingHistory();
                List<ChunkingHistoryEntry> allEntries = chunkingEntries.all();
                for (ChunkingHistoryEntry entry : allEntries) {
                    numberBasedChunking.add(entry.getSequenceNumber(), entry.getInterval().intValue(), entry.getLeftBound().intValue());
                }
                this.numberChunkingHistory = numberBasedChunking;
            }
        }
        return this.numberChunkingHistory;
    }

}
