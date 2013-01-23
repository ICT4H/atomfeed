package org.ict4h.atomfeed.motechserver;

import org.ict4htw.atomfeed.server.service.Event;
import org.ict4htw.atomfeed.server.service.EventService;
import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Component
public class EventHandler {
    @Autowired
    private EventService eventService;
    @MotechListener(subjects = "atomFeedSubject")
    public void testHandler(MotechEvent event) throws URISyntaxException {
        UUID uuid=event.getId();
        String title=event.getSubject();
        DateTime timeStamp=new DateTime();
        Object eventObject=event;
        Event _event=new Event(uuid.toString(), title, timeStamp, new URI("http://uri"),eventObject);
        eventService.notify(_event);
    }
}
