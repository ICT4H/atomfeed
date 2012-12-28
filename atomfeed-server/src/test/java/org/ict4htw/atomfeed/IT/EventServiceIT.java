package org.ict4htw.atomfeed.IT;

import org.ict4htw.atomfeed.SpringIntegrationIT;
import org.ict4htw.atomfeed.domain.EventRecord;
import org.ict4htw.atomfeed.repository.AllEventRecords;
import org.ict4htw.atomfeed.service.Event;
import org.ict4htw.atomfeed.service.EventService;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class EventServiceIT extends SpringIntegrationIT {

    @Autowired
    private EventService eventService;

    @Autowired
    private AllEventRecords allEventRecords;

    @After
    public void clearEventRecords() {
        template.deleteAll(template.loadAll(EventRecord.class));
    }

    @Test
    public void shouldNotifyEventServiceOfEventAndStoreIt() throws URISyntaxException {
        String uuid = UUID.randomUUID().toString();
        Event event = new Event(uuid, "title", DateTime.now(), "http://uri", Arrays.asList("asd", "dsa", "zzz"));

        eventService.notify(event);

        EventRecord eventRecord = allEventRecords.get(uuid);
        assertNotNull(eventRecord);
        assertEquals(eventRecord.getTitle(), event.getTitle());
    }
}
