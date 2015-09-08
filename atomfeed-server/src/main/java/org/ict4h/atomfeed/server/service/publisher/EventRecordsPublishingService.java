package org.ict4h.atomfeed.server.service.publisher;

import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.EventRecordQueueItem;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.AllEventRecordsQueue;

import java.util.ArrayList;
import java.util.List;

public class EventRecordsPublishingService {

    public static void publish(AllEventRecords allEventRecords, AllEventRecordsQueue allEventRecordsQueue) {
        List<EventRecordQueueItem> queueItemList = allEventRecordsQueue.getAll();
        List<EventRecord> eventRecords = mapToEventRecords(queueItemList);
        publishEventRecords(eventRecords, allEventRecords, allEventRecordsQueue);
    }

    private static void publishEventRecords(List<EventRecord> eventRecords, AllEventRecords allEventRecords, AllEventRecordsQueue allEventRecordsQueue) {
        for (EventRecord eventRecord : eventRecords) {
            allEventRecords.add(eventRecord);
            allEventRecordsQueue.delete(eventRecord.getUuid());
        }
    }

    private static List<EventRecord> mapToEventRecords(List<EventRecordQueueItem> queueItemList) {
        List<EventRecord> eventRecords = new ArrayList<>();
        for (EventRecordQueueItem eventRecordQueueItem : queueItemList) {
            EventRecord eventRecord = new EventRecord(eventRecordQueueItem.getUuid(), eventRecordQueueItem.getTitle(),
                    eventRecordQueueItem.getUri(), eventRecordQueueItem.getContents(), eventRecordQueueItem.getTimeStamp(), eventRecordQueueItem.getCategory());
            eventRecords.add(eventRecord);
        }
        return eventRecords;
    }
}
