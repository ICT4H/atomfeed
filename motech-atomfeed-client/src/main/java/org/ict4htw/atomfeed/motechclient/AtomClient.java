package org.ict4htw.atomfeed.motechclient;

import org.ict4htw.atomfeed.client.api.AtomFeedClient;
import org.ict4htw.atomfeed.client.api.FeedClient;
import org.ict4htw.atomfeed.client.api.data.Event;
import org.ict4htw.atomfeed.client.repository.AllFeeds;
import org.ict4htw.atomfeed.client.repository.AllMarkers;
import org.ict4htw.atomfeed.client.repository.datasource.MarkerDataSource;
import org.ict4htw.atomfeed.client.repository.datasource.WebClient;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Component
public class AtomClient {

    private URI entryURL;
    private EventToMotechEventMapper eventToMotechEventMapper;

    public EventRelay getEventRelay() {
        return eventRelay;
    }

    public void setEventRelay(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    @Autowired
    private EventRelay eventRelay;
    private FeedClient feedClient;


    @PostConstruct
    public void postConstruct(){
        System.out.println("post construct");
        System.out.println(eventRelay+"here");
    }

    public AtomClient(URI startingURL, WebClient webClient, EventToMotechEventMapper eventToMotechEventMapper) throws URISyntaxException {

        this.entryURL = startingURL;
        this.eventToMotechEventMapper = eventToMotechEventMapper;
        AllFeeds allFeeds = new AllFeeds(webClient);
        MarkerDataSource inmemoryMarkerDataSource = new InmemoryMarkerDataSource();
        feedClient=new AtomFeedClient(allFeeds, new AllMarkers(inmemoryMarkerDataSource));
    }

    @MotechListener(subjects = "testSubject")
    public void testEventListner(){
        System.out.println("fired");
    }


    public  void update() throws URISyntaxException {
        List<Event> events = feedClient.unprocessedEvents(entryURL);
        for (Event event : events) {
            eventRelay.sendEventMessage(eventToMotechEventMapper.map(event));
        }
        Event lastEvent = events.get(events.size() - 1);
        feedClient.processedTo(entryURL,lastEvent.getId());
    }

}
