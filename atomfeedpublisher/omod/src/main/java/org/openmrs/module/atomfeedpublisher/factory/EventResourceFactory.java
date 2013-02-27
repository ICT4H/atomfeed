package org.openmrs.module.atomfeedpublisher.factory;

import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistory;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.ict4htw.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4htw.atomfeed.server.repository.jdbc.PropertiesJdbcConnectionProvider;
import org.ict4htw.atomfeed.server.service.EventFeedService;
import org.ict4htw.atomfeed.server.service.EventFeedServiceImpl;
import org.ict4htw.atomfeed.server.service.EventService;
import org.ict4htw.atomfeed.server.service.EventServiceImpl;
import org.ict4htw.atomfeed.server.service.feedgenerator.FeedGenerator;
import org.ict4htw.atomfeed.server.service.feedgenerator.NumberFeedGenerator;

public class EventResourceFactory {
    public EventService getEventService(){
        AllEventRecords allEventRecords = new AllEventRecordsJdbcImpl(new PropertiesJdbcConnectionProvider());
        return new EventServiceImpl(allEventRecords);
    }

    public EventFeedService getEventFeedService(){
        AllEventRecords records = new AllEventRecordsJdbcImpl(new PropertiesJdbcConnectionProvider());
        FeedGenerator feedGenerator = new NumberFeedGenerator(records, new NumberBasedChunkingHistory());
        return new EventFeedServiceImpl(feedGenerator);
    }
}
