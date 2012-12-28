package org.ict4htw.atomfeed.IT;

import org.ict4htw.atomfeed.domain.EventRecord;
import org.ict4htw.atomfeed.repository.AllEventRecords;
import org.ict4htw.atomfeed.service.Event;
import org.ict4htw.atomfeed.service.EventService;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class EventServiceIT {

    @Autowired
    private EventService eventService;

    @Autowired
    private AllEventRecords allEventRecords;

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
