package org.ict4htw.atomfeed.server.service.feedgenerator;

import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistory;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;

public class FeedGeneratorFactory {
    public FeedGenerator getObject(AllEventRecords allEventRecords) throws Exception {
        NumberBasedChunkingHistory numberBasedChunking = new NumberBasedChunkingHistory();
        numberBasedChunking.add(1, 5, 1);
        return new FeedGeneratorBasedOnNumberBasedChunking(allEventRecords,numberBasedChunking);
    }
}
