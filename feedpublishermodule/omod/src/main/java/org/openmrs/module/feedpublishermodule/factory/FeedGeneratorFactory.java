package org.openmrs.module.feedpublishermodule.factory;

import org.ict4h.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistory;
import org.ict4h.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistoryEntry;
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
        NumberBasedChunkingHistory numberBasedChunking = new NumberBasedChunkingHistory();
        List<NumberBasedChunkingHistoryEntry> allEntries = allChunkingEntries.all(NumberBasedChunkingHistoryEntry.class);
        for (NumberBasedChunkingHistoryEntry entry : allEntries){
            numberBasedChunking.add(entry.getSeqNum(),entry.getChunkSize(),entry.getStartPosition());
        }
        return new NumberFeedGenerator(allEventRecords,numberBasedChunking);
    }
}
