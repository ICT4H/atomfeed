package org.ict4h.atomfeed.motechserver;

import org.ict4htw.atomfeed.server.service.Event;
import org.motechproject.event.MotechEvent;

public interface MotechEventToEventMapper {
    Event map(MotechEvent event);
}