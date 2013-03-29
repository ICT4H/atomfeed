package org.openmrs.module.feedpublishermodule.factory;

import org.ict4h.atomfeed.server.domain.chunking.number.NumberChunkingHistory;
import org.ict4h.atomfeed.server.domain.chunking.number.NumberChunkingHistoryEntry;
import org.ict4h.atomfeed.server.repository.jdbc.ChunkingEntriesJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.server.service.feedgenerator.FeedGenerator;
import org.ict4h.atomfeed.server.service.feedgenerator.NumberFeedGenerator;

import java.util.List;

public class FeedGeneratorFactory {
    public FeedGenerator get(JdbcConnectionProvider provider){
        AllEventRecordsJdbcImpl allEventRecords = new AllEventRecordsJdbcImpl(provider);
        ChunkingEntriesJdbcImpl allChunkingEntries = new ChunkingEntriesJdbcImpl(provider);
        allEventRecords.setSchema("");
        allChunkingEntries.setSchema("");
        NumberChunkingHistory numberBasedChunking = new NumberChunkingHistory();
        List<NumberChunkingHistoryEntry> allEntries = allChunkingEntries.all(NumberChunkingHistoryEntry.class);
        for (NumberChunkingHistoryEntry entry : allEntries){
            numberBasedChunking.add(entry.getSeqNum(),entry.getChunkSize(),entry.getStartPosition());
        }
        return new NumberFeedGenerator(allEventRecords,numberBasedChunking);
    }
}
