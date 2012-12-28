package org.ict4htw.atomfeed.mapper;

import com.sun.syndication.feed.atom.Entry;
import org.ict4htw.atomfeed.domain.EventRecord;
import org.ict4htw.atomfeed.service.Event;

import java.util.ArrayList;
import java.util.List;

public class EventRecordMapper {

    public static EventRecord map (Event event) {
        return new EventRecord(event.getUuid(), event.getTitle(),
                event.getTimeStamp(), event.getUri(), event.getEventObject());
    }

}
