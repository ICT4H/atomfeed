package org.ict4htw.atomfeed.client.api;

import org.ict4htw.atomfeed.client.domain.Marker;
import org.ict4htw.atomfeed.client.repository.AllFeeds;
import org.ict4htw.atomfeed.client.repository.AllMarkers;
import org.ict4htw.atomfeed.client.repository.datasource.InMemoryMarkers;
import org.ict4htw.atomfeed.client.repository.datasource.WebClientStub;
import org.ict4htw.atomfeed.server.repository.AllEventRecordsStub;
import org.ict4htw.atomfeed.server.repository.InMemoryEventRecordCreator;
import org.ict4htw.atomfeed.server.resource.EventResource;
import org.ict4htw.atomfeed.server.service.EventFeedService;
import org.ict4htw.atomfeed.server.service.EventService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class FeedClientImplTest {

    FeedClient feedClient;
    private InMemoryEventRecordCreator feedRecordCreator;

    @Before
    public void setup(){
        AllEventRecordsStub allEventRecords = new AllEventRecordsStub();
        EventService eventService = new EventService(allEventRecords);
        EventFeedService eventFeedService = new EventFeedService(allEventRecords);
        WebClientStub webClientStub = new WebClientStub(new EventResource(eventFeedService, eventService));
        AllFeeds allFeeds = new AllFeeds(webClientStub);
        feedRecordCreator = new InMemoryEventRecordCreator(allEventRecords);
        InMemoryMarkers markerDataSource = new InMemoryMarkers();
        markerDataSource.add("testconsumerid", new Marker(){

        });
        feedClient=new FeedClientImpl(allFeeds, new AllMarkers(markerDataSource));
    }

    @Test
    @Ignore(value = "compilation error")
    public void testUnprocessedEvents() throws Exception {
        feedRecordCreator.create(7);
//        feedClient.unprocessedEvents();
    }

    @Test
    public void testConfirmProcessed() throws Exception {

    }
}
