package org.ict4h.atomfeed.server.service;

import org.ict4h.atomfeed.server.domain.EventRecordQueueItem;
import org.ict4h.atomfeed.server.repository.AllEventRecordsQueueStub;
import org.junit.Test;

import java.net.URISyntaxException;
import java.time.LocalDateTime;

import static junit.framework.Assert.assertEquals;

public class EventServiceImplTest {
    @Test
    public void shouldNotifyEventServiceOfEventAndStoreIt() throws URISyntaxException {
        AllEventRecordsQueueStub allEventRecordsQueue = new AllEventRecordsQueueStub();
        EventService eventService = new EventServiceImpl(allEventRecordsQueue);
        String uuid = "51850820-6071-11e2-bcfd-0800200c9a66";

        Event event = new Event(uuid, "title", LocalDateTime.now(), "http://uri", "", "category");
        eventService.notify(event);
        EventRecordQueueItem eventRecord = allEventRecordsQueue.get(uuid);

        assertEventEquals(event, eventRecord);
    }

    private void assertEventEquals(Event event, EventRecordQueueItem eventRecordQueueItem) {
        assertEquals(event.getUuid(), eventRecordQueueItem.getUuid());
        assertEquals(event.getTitle(), eventRecordQueueItem.getTitle());
        assertEquals(event.getUri().toString(), eventRecordQueueItem.getUri());
        assertEquals(event.getContents(), eventRecordQueueItem.getContents());
        assertEquals(event.getCategory(), eventRecordQueueItem.getCategory());
    }

}
