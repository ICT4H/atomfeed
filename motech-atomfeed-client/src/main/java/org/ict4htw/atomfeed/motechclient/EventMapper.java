package org.ict4htw.atomfeed.motechclient;

import org.ict4htw.atomfeed.client.api.data.Event;
import org.motechproject.event.MotechEvent;
import org.springframework.stereotype.Component;

@Component
public interface EventMapper {
    MotechEvent map(Event event);
}
