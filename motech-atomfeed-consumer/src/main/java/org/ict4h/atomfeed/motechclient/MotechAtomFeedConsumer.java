package org.ict4h.atomfeed.motechclient;

import org.ict4h.atomfeed.client.api.FeedClient;
import org.ict4h.atomfeed.client.api.data.Event;
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
import java.util.HashMap;
import java.util.List;

public class MotechAtomFeedConsumer {

    public static final String EVENT_FROM_OPEN_MRS = "eventFromOpenMRS";
    private URI entryURL;
    private MotechSchedulerService schedulerService;
    private static final String ATOM_UPDATE_MESSAGE="atomUpdateMessage";
    private String cronExpression;
    private EventRelay eventRelay;
    private FeedClient feedClient;

    @PostConstruct
    public void startScheduler(){
        schedulerService.safeScheduleJob(new CronSchedulableJob(new MotechEvent(ATOM_UPDATE_MESSAGE),cronExpression));
    }

    @PreDestroy
    public void stopScheduler(){
        schedulerService.safeUnscheduleAllJobs(ATOM_UPDATE_MESSAGE);
    }

    public MotechAtomFeedConsumer(URI startingURL, FeedClient feedClient,
                                  String cronExpression, EventRelay eventRelay, MotechSchedulerService schedulerService) throws URISyntaxException {
        this.cronExpression = cronExpression;
        this.entryURL = startingURL;
        this.eventRelay = eventRelay;
        this.schedulerService = schedulerService;
        this.feedClient = feedClient;
    }

    @MotechListener(subjects = ATOM_UPDATE_MESSAGE)
    public void updateEvents(MotechEvent event) throws URISyntaxException {
        Thread thread = Thread.currentThread();
        ClassLoader oldContextClassLoader = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(this.getClass().getClassLoader());
            this.update();
        }catch (Exception ex){
            throw new RuntimeException(String.format("Event Update Failure. Error %s",ex.getMessage()), ex);
        }
        finally {
            thread.setContextClassLoader(oldContextClassLoader);
        }
    }

    private void update() throws URISyntaxException {
        List<Event> events = feedClient.unprocessedEvents(entryURL);
        System.out.println("marker - before processing events");
        if(!events.isEmpty()){
            System.out.println("marker - event. In event processor");
            for (Event event : events) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("contents", event.getContent());
                eventRelay.sendEventMessage(new MotechEvent(EVENT_FROM_OPEN_MRS,map));
            }
            Event lastEvent = events.get(events.size() - 1);
            feedClient.processedTo(entryURL,lastEvent.getId());
        }
    }
}
