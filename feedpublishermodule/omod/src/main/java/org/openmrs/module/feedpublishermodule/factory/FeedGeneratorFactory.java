package org.openmrs.module.feedpublishermodule.factory;

import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistory;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.ict4htw.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4htw.atomfeed.server.repository.jdbc.PropertiesJdbcConnectionProvider;
import org.ict4htw.atomfeed.server.service.feedgenerator.FeedGenerator;
import org.ict4htw.atomfeed.server.service.feedgenerator.NumberFeedGenerator;

public class FeedGeneratorFactory {
    public FeedGenerator get(){
        AllEventRecords eventsRecord = new AllEventRecordsJdbcImpl(new PropertiesJdbcConnectionProvider());
        return new NumberFeedGenerator(eventsRecord, new NumberBasedChunkingHistory());
    }
}
