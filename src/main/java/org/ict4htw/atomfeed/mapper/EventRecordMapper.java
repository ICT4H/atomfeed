package org.ict4htw.atomfeed.mapper;

import org.ict4htw.atomfeed.domain.EventRecord;
import org.ict4htw.atomfeed.service.Event;

public class EventRecordMapper {

    public static EventRecord map (Event event) {
        return new EventRecord(event.getUuid(), event.getTitle(),
                event.getTimeStamp(), event.getUri(), event.getEventObject());
    }

}
