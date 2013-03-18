package org.ict4h.atomfeed.server.service.feedgenerator;

import java.util.List;

import org.ict4h.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistory;
import org.ict4h.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistoryEntry;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.ChunkingHistories;

public class FeedGeneratorFactory {
    public FeedGenerator getFeedGenerator(AllEventRecords allEventRecords, ChunkingHistories allChunkingEntries) throws Exception {
        //TODO: more work here. NumberBasedChunkingHistory would not work over time strategy 
    	NumberBasedChunkingHistory numberBasedChunking = new NumberBasedChunkingHistory();
        List<NumberBasedChunkingHistoryEntry> allEntries = allChunkingEntries.all(NumberBasedChunkingHistoryEntry.class);
        for (NumberBasedChunkingHistoryEntry entry : allEntries){
            numberBasedChunking.add(entry.getSeqNum(),entry.getChunkSize(),entry.getStartPosition());
        }
        return new NumberFeedGenerator(allEventRecords,numberBasedChunking);
    }
}
