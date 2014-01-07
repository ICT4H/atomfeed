package org.ict4h.atomfeed.server.domain.chunking.number;

import org.ict4h.atomfeed.server.domain.EventRecordsOffsetMarker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NumberChunkingHistory {

    List<NumberChunkingHistoryEntry> chunkingHistoryEntries = new ArrayList<>();

    public void add(int seqNum, int chunkSize, int startPosition) {
        closeOffCurrent(startPosition - 1);
        NumberChunkingHistoryEntry entryNumberBasedHistory = new NumberChunkingHistoryEntry(seqNum, chunkSize, startPosition);
        chunkingHistoryEntries.add(entryNumberBasedHistory);
        Collections.sort(chunkingHistoryEntries, new Comparator<NumberChunkingHistoryEntry>() {
            public int compare(NumberChunkingHistoryEntry e1, NumberChunkingHistoryEntry e2) {
                return (e1.getSequenceNumber() > e2.getSequenceNumber()) ? 1 : (e1.getSequenceNumber() == e2.getSequenceNumber() ? 0 : -1);
            }
        });
    }

    public NumberRange findRange(Integer feedId, int upperBound) {
        int feedsSofar = 0;
        for (NumberChunkingHistoryEntry historyEntry : chunkingHistoryEntries) {
            int feedCount = historyEntry.getFeedCount(upperBound);
            if (historyEntry.isOpen() || (feedsSofar + feedCount >= feedId)) {
                return historyEntry.getRange(feedId - feedsSofar);
            } else {
                feedsSofar += feedCount;
            }
        }
        return null;
    }

    public int getNumberOfFeeds(int limit) {
        int feedCount = 0;
        for (NumberChunkingHistoryEntry historyEntry : chunkingHistoryEntries) {
            feedCount += historyEntry.getFeedCount(limit);
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
        int size = chunkingHistoryEntries.size();
        return (size > 0) ? chunkingHistoryEntries.get(size - 1) : null;
    }
}
