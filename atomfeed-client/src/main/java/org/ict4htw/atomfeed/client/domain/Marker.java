package org.ict4htw.atomfeed.client.domain;

import java.net.URI;

public class Marker {
    private String entryId;
    private URI workingFeed;

    public Marker(URI feedUri, String entryId) {
        this.workingFeed = feedUri;
        this.entryId = entryId;
	}

	public String getEntryId() {
        return entryId;
    }
	
	public URI getFeedUri() {
		return workingFeed;
	}

    public Marker updateTo(String entryId) {
        return new Marker(workingFeed, entryId);
    }
}