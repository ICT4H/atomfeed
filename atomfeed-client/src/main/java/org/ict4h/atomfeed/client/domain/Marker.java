package org.ict4h.atomfeed.client.domain;

import java.net.URI;

public class Marker {
    private String lastReadEntryId;
    private URI feedUri;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Marker)) return false;

        Marker marker = (Marker) o;

        if (feedURIForLastReadEntry != null ? !feedURIForLastReadEntry.equals(marker.feedURIForLastReadEntry) : marker.feedURIForLastReadEntry != null)
            return false;
        if (feedUri != null ? !feedUri.equals(marker.feedUri) : marker.feedUri != null) return false;
        if (lastReadEntryId != null ? !lastReadEntryId.equals(marker.lastReadEntryId) : marker.lastReadEntryId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = lastReadEntryId != null ? lastReadEntryId.hashCode() : 0;
        result = 31 * result + (feedUri != null ? feedUri.hashCode() : 0);
        result = 31 * result + (feedURIForLastReadEntry != null ? feedURIForLastReadEntry.hashCode() : 0);
        return result;
    }
}