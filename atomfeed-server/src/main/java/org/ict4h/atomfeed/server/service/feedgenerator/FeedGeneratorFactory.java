package org.ict4h.atomfeed.server.service.feedgenerator;

import java.util.*;

import org.ict4h.atomfeed.server.domain.chunking.number.NumberChunkingHistory;
import org.ict4h.atomfeed.server.domain.chunking.number.NumberChunkingHistoryEntry;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeChunkingHistory;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeChunkingHistoryEntry;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;

public class FeedGeneratorFactory {

    public static final String NumberBasedChunkingStrategy = "number";

    public FeedGenerator getFeedGenerator(AllEventRecords allEventRecords, ChunkingEntries allChunkingEntries) throws Exception {
        return get(getChunkingStrategy(), allEventRecords, allChunkingEntries);
    }

    private FeedGenerator get(String chunkingStrategy, AllEventRecords allEventRecords, ChunkingEntries allChunkingEntries) {
        return chunkingStrategy.equals(NumberBasedChunkingStrategy) ?
                getNumberBasedFeedGenerator(allEventRecords,allChunkingEntries) :
                getTimeBasedFeedGenerator(allEventRecords,allChunkingEntries);
    }

    private FeedGenerator getTimeBasedFeedGenerator(AllEventRecords allEventRecords, ChunkingEntries allChunkingEntries) {
        List<TimeChunkingHistoryEntry> allEntries = allChunkingEntries.all(TimeChunkingHistoryEntry.class);
        TimeChunkingHistory timeChunkingHistory = new TimeChunkingHistory(allEntries);
        return new TimeFeedGenerator(timeChunkingHistory,allEventRecords);
    }

    //TODO:This logic of retrieving keys using a bundle is duplicated. Use a ResourceWrapper.
    private String getChunkingStrategy() {
        ResourceBundle bundle;
        try
        {
            bundle = ResourceBundle.getBundle("atomfeed");
        }catch (MissingResourceException ex){
            return NumberBasedChunkingStrategy;
        }
        if(bundle.containsKey("chunking.strategy")){
             return bundle.getString("chunking.strategy").toLowerCase();
        }
        return NumberBasedChunkingStrategy;
    }

    private FeedGenerator getNumberBasedFeedGenerator(AllEventRecords allEventRecords, ChunkingEntries allChunkingEntries) {
        NumberChunkingHistory numberBasedChunking = new NumberChunkingHistory();
        List<NumberChunkingHistoryEntry> allEntries = allChunkingEntries.all(NumberChunkingHistoryEntry.class);
        for (NumberChunkingHistoryEntry entry : allEntries){
            numberBasedChunking.add(entry.getSeqNum(),entry.getChunkSize(),entry.getStartPosition());
        }
        return new NumberFeedGenerator(allEventRecords,numberBasedChunking);
    }


}
