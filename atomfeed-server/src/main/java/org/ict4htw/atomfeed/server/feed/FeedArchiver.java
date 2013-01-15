package org.ict4htw.atomfeed.server.feed;

import java.util.List;
import java.util.UUID;

import org.ict4htw.atomfeed.server.domain.EventArchive;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeedArchiver {
	
	private AllEventRecords eventRecords;
	
	//needs to read/passed from config
	private final int ENTRIES_PER_FEED = 5;
	
    @Autowired
	public FeedArchiver(AllEventRecords eventRecords) {
    	 this.eventRecords = eventRecords;
	}

	public void archiveFeeds() {
		if (eventRecords.getTotalCount() == 0) {
			return;
		}
		
		int unarchivedCount = eventRecords.getUnarchivedEventsCount();
		if (unarchivedCount == 0) {
			throw new RuntimeException("Recent Feed must have one or more events");
		}
		
		if(isWorkingFeed(unarchivedCount)) {
			return;
		}
		
		int possiblefeedCount = unarchivedCount / ENTRIES_PER_FEED;
		for (int idx = 0; idx < possiblefeedCount; idx++) {
			List<EventRecord> unarchivedEvents = eventRecords.getUnarchivedEvents(ENTRIES_PER_FEED);
			archive(unarchivedEvents);
		}
		
	}

	private void archive(List<EventRecord> unarchivedEvents) {
		EventArchive newArchive = createNewArchive();
		newArchive.addEvents(unarchivedEvents);
		eventRecords.save(unarchivedEvents);
	}

	private EventArchive createNewArchive() {
		EventArchive eventArchive = new EventArchive(UUID.randomUUID().toString(), getParentArchiveId());
		eventRecords.save(eventArchive);
		return eventArchive;
	}

	private String getParentArchiveId() {
		EventArchive latestArchive = eventRecords.getLatestArchive();
		return (latestArchive != null) ? latestArchive.getArchiveId() : null;
	}

	private boolean isWorkingFeed(int eventsCount) {
		return (eventsCount <= ENTRIES_PER_FEED); //all events are in recent feed
	}
}
