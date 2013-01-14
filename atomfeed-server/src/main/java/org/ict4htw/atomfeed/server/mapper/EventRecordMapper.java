package org.ict4htw.atomfeed.server.mapper;

import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.service.Event;

public class EventRecordMapper {

    public static EventRecord map (Event event) {
        return new EventRecord(event.getUuid(), event.getTitle(),
                event.getUri(), event.getEventObject());
    }

}
