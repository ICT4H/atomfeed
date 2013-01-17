package org.ict4htw.atomfeed.server.service;

import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.repository.AllEventRecordsStub;
import org.joda.time.DateTime;
import org.junit.Test;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class EventServiceTest {
    @Test
    public void shouldNotifyEventServiceOfEventAndStoreIt() throws URISyntaxException {
        AllEventRecordsStub allEventRecords = new AllEventRecordsStub();
        EventService eventService = new EventService(allEventRecords);
        String uuid = "51850820-6071-11e2-bcfd-0800200c9a66";
        List<String> serializableObject = Collections.emptyList();

        Event event = new Event(uuid, "title", new DateTime(123), "http://uri", serializableObject);
        eventService.notify(event);
        EventRecord eventRecord = allEventRecords.get(uuid);

        assertEquals(eventRecord.getTitle(), event.getTitle());
    }
}
