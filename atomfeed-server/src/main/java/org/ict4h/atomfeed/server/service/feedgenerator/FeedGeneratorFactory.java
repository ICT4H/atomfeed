package org.ict4h.atomfeed.server.service.feedgenerator;

import java.util.*;

import org.ict4h.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistory;
import org.ict4h.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistoryEntry;
import org.ict4h.atomfeed.server.domain.timebasedchunkingconfiguration.TimeBasedChunkingHistory;
import org.ict4h.atomfeed.server.domain.timebasedchunkingconfiguration.TimeBasedChunkingHistoryEntry;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;

public class FeedGeneratorFactory {
    public FeedGenerator getFeedGenerator(AllEventRecords allEventRecords, ChunkingEntries allChunkingEntries) throws Exception {
        return get(getChunkingStrategy(), allEventRecords, allChunkingEntries);
    }

    private FeedGenerator get(String chunkingStrategy, AllEventRecords allEventRecords, ChunkingEntries allChunkingEntries) {
        return chunkingStrategy == "number" ?
                getNumberBasedFeedGenerator(allEventRecords,allChunkingEntries) :
                getTimeBasedFeedGenerator(allEventRecords,allChunkingEntries);
    }

    private FeedGenerator getTimeBasedFeedGenerator(AllEventRecords allEventRecords, ChunkingEntries allChunkingEntries) {
        List<TimeBasedChunkingHistoryEntry> allEntries = allChunkingEntries.all(TimeBasedChunkingHistoryEntry.class);
        TimeBasedChunkingHistory timeBasedChunkingHistory = new TimeBasedChunkingHistory(allEntries);
        return new TimeFeedGenerator(timeBasedChunkingHistory,allEventRecords);
    }

    //TODO:This logic of retrieving keys using a bundle is duplicated. Use a ResourceWrapper.
    private String getChunkingStrategy() {
        ResourceBundle bundle;
        try
        {
            bundle = ResourceBundle.getBundle("atomfeed");
        }catch (MissingResourceException ex){
            return "number";
        }
        if(bundle.containsKey("chunking.strategy")){
             return bundle.getString("chunking.strategy").toLowerCase();
        }
        return "number";
    }

    private FeedGenerator getNumberBasedFeedGenerator(AllEventRecords allEventRecords, ChunkingEntries allChunkingEntries) {
        NumberBasedChunkingHistory numberBasedChunking = new NumberBasedChunkingHistory();
        List<NumberBasedChunkingHistoryEntry> allEntries = allChunkingEntries.all(NumberBasedChunkingHistoryEntry.class);
        for (NumberBasedChunkingHistoryEntry entry : allEntries){
            numberBasedChunking.add(entry.getSeqNum(),entry.getChunkSize(),entry.getStartPosition());
        }
        return new NumberFeedGenerator(allEventRecords,numberBasedChunking);
    }


}
