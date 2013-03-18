package org.openmrs.module.feedpublishermodule.factory;

import org.hibernate.SessionFactory;
import org.ict4h.atomfeed.server.service.EventService;
import org.ict4h.atomfeed.server.service.EventServiceImpl;
import org.openmrs.module.feedpublishermodule.repository.hibernate.*;

public class EventServiceFactory {
    public EventService get(SessionFactory sessionFactory){
        return new EventServiceImpl(new EventRecordDAO(sessionFactory));
    }
}
