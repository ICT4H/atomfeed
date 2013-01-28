package org.ict4htw.atomfeed.server.service.feedgenerator;

import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistory;
import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistoryEntry;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.ict4htw.atomfeed.server.repository.ChunkingHistories;

import java.util.List;

public class FeedGeneratorFactory {
    public FeedGenerator getObject(AllEventRecords allEventRecords, ChunkingHistories allChunkingEntries) throws Exception {
        NumberBasedChunkingHistory numberBasedChunking = new NumberBasedChunkingHistory();
        List<NumberBasedChunkingHistoryEntry> allEntries = allChunkingEntries.all(NumberBasedChunkingHistoryEntry.class);
        for (NumberBasedChunkingHistoryEntry entry : allEntries){
            numberBasedChunking.add(entry.getSeqNum(),entry.getChunkSize(),entry.getStartPosition());
        }
        return new NumberFeedGenerator(allEventRecords,numberBasedChunking);
    }
}
