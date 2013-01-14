package org.ict4htw.atomfeed.server.service;

import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.repository.AllEventRecordsStub;
import org.joda.time.DateTime;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class EventServiceTest {
    @Test
    public void shouldNotifyEventServiceOfEventAndStoreIt() throws URISyntaxException {
        AllEventRecordsStub allEventRecords = new AllEventRecordsStub();
        EventService eventService = new EventService(allEventRecords);
        String uuid = UUID.randomUUID().toString();
        Event event = new Event(uuid, "title", DateTime.now(), "http://uri", Arrays.asList("asd", "dsa", "zzz"));

        eventService.notify(event);

        EventRecord eventRecord = allEventRecords.get(uuid);
        assertNotNull(eventRecord);
        assertEquals(eventRecord.getTitle(), event.getTitle());
    }
}
