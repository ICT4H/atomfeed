package org.openmrs.module.feedpublishermodule.factory;

import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.ChunkingEntriesJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.server.service.feedgenerator.FeedGenerator;

public class FeedGeneratorFactory {
    public FeedGenerator get(JdbcConnectionProvider provider){
        AllEventRecordsJdbcImpl allEventRecords = new AllEventRecordsJdbcImpl(provider);
        ChunkingEntriesJdbcImpl allChunkingEntries = new ChunkingEntriesJdbcImpl(provider);
        allEventRecords.setSchema("");
        allChunkingEntries.setSchema("");
        return new org.ict4h.atomfeed.server.service.feedgenerator.FeedGeneratorFactory().getFeedGenerator(allEventRecords,allChunkingEntries);
    }
}
