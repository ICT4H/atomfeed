package org.ict4h.atomfeed.client.domain;

import java.net.URI;

public class Marker {

    private URI feedUri;
    private String lastReadEntryId;
    private URI feedURIForLastReadEntry;

    public Marker(URI feedUri, String lastReadEntryId, URI feedURIForLastReadEntry) {
        this.feedUri = feedUri;
        this.lastReadEntryId = lastReadEntryId;
        this.feedURIForLastReadEntry = feedURIForLastReadEntry;
    }

	public String getLastReadEntryId() {
        return lastReadEntryId;
    }

    public URI getFeedURIForLastReadEntry() {
        return feedURIForLastReadEntry;
    }

	public URI getFeedUri() {
		return feedUri;
	}
}