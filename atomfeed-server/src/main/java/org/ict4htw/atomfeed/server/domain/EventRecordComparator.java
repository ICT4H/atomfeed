package org.ict4htw.atomfeed.server.domain;

import java.util.Comparator;

public class EventRecordComparator implements Comparator<EventRecord> {
    @Override
    public int compare(EventRecord o1, EventRecord o2) {
        return o1.getTimeStamp().compareTo(o2.getTimeStamp());
    }
}