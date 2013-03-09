package org.openmrs.module.feedpublishermodule.factory;

import org.hibernate.SessionFactory;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.ict4htw.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4htw.atomfeed.server.repository.jdbc.JdbcConnectionProvider;
import org.ict4htw.atomfeed.server.service.EventService;
import org.ict4htw.atomfeed.server.service.EventServiceImpl;
import repository.hibernate.EventRecordDAO;

public class EventServiceFactory {
    public EventService get(SessionFactory sessionFactory){
        return new EventServiceImpl(new EventRecordDAO(sessionFactory));
    }
}
