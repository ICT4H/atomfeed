package org.ict4h.atomfeed.server.service;

import org.ict4h.atomfeed.server.domain.EventRecordQueueItem;
import org.ict4h.atomfeed.server.repository.AllEventRecordsQueueStub;
import org.ict4h.atomfeed.server.repository.AllEventRecordsStub;
import org.joda.time.DateTime;
import org.junit.Test;

import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class EventServiceImplTest {
    @Test
    public void shouldNotifyEventServiceOfEventAndStoreIt() throws URISyntaxException {
        AllEventRecordsQueueStub allEventRecordsQueue = new AllEventRecordsQueueStub();
        EventService eventService = new EventServiceImpl(allEventRecordsQueue);
        String uuid = "51850820-6071-11e2-bcfd-0800200c9a66";

        Event event = new Event(uuid, "title", new DateTime(123), "http://uri", "", "category");
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


    @Test
    public void testJodaTime() {
//        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSZ");
        Date date = DateTime.now().toDate();
        Timestamp dbDate = new Timestamp(date.getTime());
//        System.out.println(date.getTime());
//        System.out.println(dbDate.getTime());
        assertEquals(date.getTime(),dbDate.getTime());
    }
}
