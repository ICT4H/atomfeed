package org.ict4htw.atomfeed.server.mapper;

import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class EventRecordMapperTest {

    @Test
    public void shouldMapEventToEventRecord() throws URISyntaxException {
        Event event = new Event("uuid", "title", DateTime.now(), "http://uri", new ArrayList<String>());

        EventRecord eventRecord = EventRecordMapper.map(event);

        assertEquals(event.getUuid(), eventRecord.getUuid());
        assertEquals(event.getTitle(), eventRecord.getTitle());
//        assertEquals(event.getTimeStamp().getMillis(), eventRecord.getTimeStamp().get);
        assertEquals(event.getUri().toString(), eventRecord.getUri());
    }
}
