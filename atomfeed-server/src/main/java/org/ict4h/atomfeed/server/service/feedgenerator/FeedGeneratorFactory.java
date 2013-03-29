package org.ict4h.atomfeed.server.service.feedgenerator;

import java.util.*;

import org.ict4h.atomfeed.server.domain.chunking.ChunkingHistoryEntry;
import org.ict4h.atomfeed.server.domain.chunking.number.NumberChunkingHistory;
import org.ict4h.atomfeed.server.domain.chunking.number.NumberChunkingHistoryEntry;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeChunkingHistory;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeChunkingHistoryEntry;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;

public class FeedGeneratorFactory {

    public static final String NumberBasedChunkingStrategy = "number";

    public FeedGenerator getFeedGenerator(AllEventRecords allEventRecords, ChunkingEntries allChunkingEntries){
        return get(getChunkingStrategy(), allEventRecords, allChunkingEntries);
    }

    private FeedGenerator get(String chunkingStrategy, AllEventRecords allEventRecords, ChunkingEntries allChunkingEntries) {
        return chunkingStrategy.equals(NumberBasedChunkingStrategy) ?
                getNumberBasedFeedGenerator(allEventRecords,allChunkingEntries) :
                getTimeBasedFeedGenerator(allEventRecords,allChunkingEntries);
    }

    private FeedGenerator getTimeBasedFeedGenerator(AllEventRecords allEventRecords, ChunkingEntries allChunkingEntries) {
        TimeChunkingHistory timeChunkingHistory = new TimeChunkingHistory();
        List<ChunkingHistoryEntry> allEntries = allChunkingEntries.all();
        for(ChunkingHistoryEntry entry : allEntries){
            timeChunkingHistory.add(entry.getInterval(),entry.getLeftBound());
        }
        return new TimeFeedGenerator(timeChunkingHistory,allEventRecords);
    }

    private FeedGenerator getNumberBasedFeedGenerator(AllEventRecords allEventRecords, ChunkingEntries allChunkingEntries) {
        NumberChunkingHistory numberBasedChunking = new NumberChunkingHistory();
        List<ChunkingHistoryEntry> allEntries = allChunkingEntries.all();
        for (ChunkingHistoryEntry entry : allEntries){
            numberBasedChunking.add(entry.getSequenceNumber(), entry.getInterval().intValue(), entry.getLeftBound().intValue());
        }
        return new NumberFeedGenerator(allEventRecords,numberBasedChunking);
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
}
