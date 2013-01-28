package org.ict4htw.atomfeed.client.domain;

import java.net.URI;

public class Marker {
    private String entryId;
    private URI feedUri;

    public Marker(URI feedUri, String entryId) {
        this.feedUri = feedUri;
        this.entryId = entryId;
	}

	public String getEntryId() {
        return entryId;
    }
	
	public URI getFeedUri() {
		return feedUri;
	}
}