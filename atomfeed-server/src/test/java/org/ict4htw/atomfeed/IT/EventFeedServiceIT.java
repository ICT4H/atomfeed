package org.ict4htw.atomfeed.IT;

import com.sun.syndication.feed.atom.Feed;
import org.ict4htw.atomfeed.SpringIntegrationIT;
import org.ict4htw.atomfeed.domain.EventRecord;
import org.ict4htw.atomfeed.repository.AllEventRecords;
import org.ict4htw.atomfeed.service.EventFeedService;
import org.ict4htw.atomfeed.util.Util;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.URISyntaxException;

public class EventFeedServiceIT extends SpringIntegrationIT {

    @Autowired
    private AllEventRecords allEventRecords;

    private EventFeedService eventFeedService;

    @Before
    public void setupEventRecords() throws URISyntaxException {
        EventRecord eventRecordAdded1 = new EventRecord("uuid1", "title", DateTime.now(), new URI("http://uri"), "asdasd");
        EventRecord eventRecordAdded2 = new EventRecord("uuid2", "title", DateTime.now(), new URI("http://uri"), "dadsas");
        EventRecord eventRecordAdded3 = new EventRecord("uuid3", "title", DateTime.now(), new URI("http://uri"), "asdasd");
        EventRecord eventRecordAdded4 = new EventRecord("uuid4", "title", DateTime.now(), new URI("http://uri"), "asdasd");
        EventRecord eventRecordAdded5 = new EventRecord("uuid5", "title", DateTime.now(), new URI("http://uri"), "asdasd");
        EventRecord eventRecordAdded6 = new EventRecord("uuid6", "title", DateTime.now(), new URI("http://uri"), "asdasd");
        EventRecord eventRecordAdded7 = new EventRecord("uuid7", "title", DateTime.now(), new URI("http://uri"), "asdasd");

        template.save(eventRecordAdded1);
        template.save(eventRecordAdded2);
        template.save(eventRecordAdded3);
        template.save(eventRecordAdded4);
        template.save(eventRecordAdded5);
        template.save(eventRecordAdded6);
        template.save(eventRecordAdded7);
    }

    @After
    public void cleanEventRecords() {
        template.deleteAll(template.loadAll(EventRecord.class));
    }

    @Test
    public void shouldGetRecentFeed() {
        eventFeedService = new EventFeedService("http://hostname/events/recent", allEventRecords);
        Feed feed = eventFeedService.getRecentFeed();
        System.out.println(Util.stringifyFeed(feed));
    }

    @Test
    public void shouldGetEventFeed() {
        eventFeedService = new EventFeedService("http://hostname/events/5,10", allEventRecords);
        Feed feed = eventFeedService.getEventFeed(5, 10);
        System.out.println(Util.stringifyFeed(feed));
    }
}
