package org.ict4htw.atomfeed.mapper;

import org.ict4htw.atomfeed.domain.EventRecord;
import org.ict4htw.atomfeed.service.Event;
import org.joda.time.DateTime;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static junit.framework.Assert.assertEquals;

public class EventRecordMapperTest {

    @Test
    public void shouldMapEventToEventRecord() throws URISyntaxException {
        Event event = new Event("uuid", "title", DateTime.now(), "http://uri", new Object());

        EventRecord eventRecord = EventRecordMapper.map(event);

        assertEquals(event.getUuid(), eventRecord.getUuid());
    }
}
