package org.openmrs.module.feedpublishermodule.factory;

import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistory;
import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistoryEntry;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.ict4htw.atomfeed.server.repository.ChunkingHistories;
import org.ict4htw.atomfeed.server.repository.jdbc.AllChunkingEntriesJdbcImpl;
import org.ict4htw.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4htw.atomfeed.server.repository.jdbc.PropertiesJdbcConnectionProvider;
import org.ict4htw.atomfeed.server.service.feedgenerator.FeedGenerator;
import org.ict4htw.atomfeed.server.service.feedgenerator.NumberFeedGenerator;

import java.util.List;

public class FeedGeneratorFactory {
    public FeedGenerator get(){
        PropertiesJdbcConnectionProvider provider = new PropertiesJdbcConnectionProvider();
        AllEventRecords allEventRecords = new AllEventRecordsJdbcImpl(provider);
        ChunkingHistories allChunkingEntries = new AllChunkingEntriesJdbcImpl(provider);
        NumberBasedChunkingHistory numberBasedChunking = new NumberBasedChunkingHistory();

        List<NumberBasedChunkingHistoryEntry> allEntries = allChunkingEntries.all(NumberBasedChunkingHistoryEntry.class);
        for (NumberBasedChunkingHistoryEntry entry : allEntries){
            numberBasedChunking.add(entry.getSeqNum(),entry.getChunkSize(),entry.getStartPosition());
        }
        return new NumberFeedGenerator(allEventRecords,numberBasedChunking);
    }
}
