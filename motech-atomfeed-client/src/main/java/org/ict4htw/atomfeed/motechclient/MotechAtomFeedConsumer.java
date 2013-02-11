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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Component
public class MotechAtomFeedConsumer {

    private URI entryURL;
    private EventMapper eventMapper;
    private MotechSchedulerService schedulerService;
    private static String ATOM_UPDATE_MESSAGE="atomUpdateMessage";
    private String cronExpression;
    private EventRelay eventRelay;
    private FeedClient feedClient;

    @PostConstruct
    public void startScheduler(){
        schedulerService.scheduleJob(
                new CronSchedulableJob(
                        new MotechEvent(ATOM_UPDATE_MESSAGE),
                        cronExpression
                ));
    }

    @PreDestroy
    public void stopScheduler(){
        schedulerService.safeUnscheduleAllJobs(ATOM_UPDATE_MESSAGE);
    }

    public MotechAtomFeedConsumer(URI startingURL, WebClient webClient, EventMapper eventMapper,
                                  String cronExpression, EventRelay eventRelay, MotechSchedulerService schedulerService) throws URISyntaxException {
        this.cronExpression = cronExpression;
        this.entryURL = startingURL;
        this.eventRelay = eventRelay;
        this.schedulerService = schedulerService;
        this.eventMapper = eventMapper;
        AllFeeds allFeeds = new AllFeeds(webClient);
        MarkerDataSource inmemoryMarkerDataSource = new InmemoryMarkerDataSource();
        feedClient=new AtomFeedClient(allFeeds, new AllMarkers(inmemoryMarkerDataSource));
    }

    @MotechListener(subjects = "atomUpdateMessage")
    public void updateEvents(MotechEvent event) throws URISyntaxException {
        this.update();
    }

    private void update() throws URISyntaxException {
        List<Event> events = feedClient.unprocessedEvents(entryURL);
        for (Event event : events) {
            eventRelay.sendEventMessage(eventMapper.map(event));
        }
        Event lastEvent = events.get(events.size() - 1);
        feedClient.processedTo(entryURL,lastEvent.getId());
    }
}
