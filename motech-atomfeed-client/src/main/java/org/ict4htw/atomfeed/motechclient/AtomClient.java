package org.ict4htw.atomfeed.motechclient;

import org.ict4htw.atomfeed.client.api.AtomFeedClient;
import org.ict4htw.atomfeed.client.api.FeedClient;
import org.ict4htw.atomfeed.client.api.data.Event;
import org.ict4htw.atomfeed.client.repository.AllFeeds;
import org.ict4htw.atomfeed.client.repository.AllMarkers;
import org.ict4htw.atomfeed.client.repository.datasource.MarkerDataSource;
import org.ict4htw.atomfeed.client.repository.datasource.WebClient;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Component
public class AtomClient {

    private URI entryURL;
    private EventToMotechEventMapper eventToMotechEventMapper;
    @Autowired
    private MotechSchedulerService schedulerService;
    private static String ATOM_UPDATE_MESSAGE="atomUpdateMessage";
    private String chronExpression;

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
    public void startScheduler(){
        schedulerService.scheduleJob(
                new CronSchedulableJob(
                        new MotechEvent(ATOM_UPDATE_MESSAGE),
                        chronExpression
                ));
    }

    @PreDestroy
    public void stopScheduler(){
        schedulerService.safeUnscheduleAllJobs(ATOM_UPDATE_MESSAGE);
    }

    public AtomClient(URI startingURL,
                      WebClient webClient,
                      EventToMotechEventMapper eventToMotechEventMapper,
                      String chronExpression) throws URISyntaxException {
        this.chronExpression=chronExpression;
        this.entryURL = startingURL;
        this.eventToMotechEventMapper = eventToMotechEventMapper;
        AllFeeds allFeeds = new AllFeeds(webClient);
        MarkerDataSource inmemoryMarkerDataSource = new InmemoryMarkerDataSource();
        feedClient=new AtomFeedClient(allFeeds, new AllMarkers(inmemoryMarkerDataSource));
    }

    @MotechListener(subjects = "atomUpdateMessage")
    public void updateEvents(MotechEvent event) throws URISyntaxException {
        this.update();
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
