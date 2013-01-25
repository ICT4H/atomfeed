package org.ict4h.atomfeed.motechserver;

import org.ict4htw.atomfeed.server.service.EventService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

@Component
public class EventHandler {
    @Autowired
    private EventService eventService;
    private MotechEventToEventMapper eventMapper;


    public EventHandler(EventService eventService, MotechEventToEventMapper eventMapper){
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @MotechListener(subjects = "*")
    public void handle(MotechEvent event) throws URISyntaxException {
        eventService.notify(eventMapper.map(event));
    }
}