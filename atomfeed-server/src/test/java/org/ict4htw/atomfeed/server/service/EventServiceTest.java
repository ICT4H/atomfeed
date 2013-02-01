package org.ict4htw.atomfeed.server.service;

import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.repository.AllEventRecordsStub;
import org.joda.time.DateTime;
import org.junit.Test;

import java.net.URISyntaxException;

import static junit.framework.Assert.assertEquals;

public class EventServiceTest {
    @Test
    public void shouldNotifyEventServiceOfEventAndStoreIt() throws URISyntaxException {
        AllEventRecordsStub allEventRecords = new AllEventRecordsStub();
        EventService eventService = new EventService(allEventRecords);
        String uuid = "51850820-6071-11e2-bcfd-0800200c9a66";

        Event event = new Event(uuid, "title", new DateTime(123), "http://uri", "");
        eventService.notify(event);
        EventRecord eventRecord = allEventRecords.get(uuid);

        assertEventEquals(event, eventRecord);
    }

    private void assertEventEquals(Event event, EventRecord eventRecord) {
        assertEquals(event.getUuid(), eventRecord.getUuid());
        assertEquals(event.getTitle(), eventRecord.getTitle());
        assertEquals(event.getUri().toString(), eventRecord.getUri());
        assertEquals(event.getContents(), eventRecord.getContents());
    }
}
