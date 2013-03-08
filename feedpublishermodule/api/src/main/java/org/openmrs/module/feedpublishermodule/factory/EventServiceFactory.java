package org.openmrs.module.feedpublishermodule.factory;

import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.ict4htw.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4htw.atomfeed.server.repository.jdbc.JdbcConnectionProvider;
import org.ict4htw.atomfeed.server.service.EventService;
import org.ict4htw.atomfeed.server.service.EventServiceImpl;

public class EventServiceFactory {
    public EventService get(JdbcConnectionProvider provider){
        AllEventRecordsJdbcImpl eventRecords = new AllEventRecordsJdbcImpl(provider);
        eventRecords.setSchema("");
        return new EventServiceImpl(eventRecords);
    }
}
