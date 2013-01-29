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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Marker marker = (Marker) o;

        if (entryId != null ? !entryId.equals(marker.entryId) : marker.entryId != null) return false;
        if (feedUri != null ? !feedUri.equals(marker.feedUri) : marker.feedUri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entryId != null ? entryId.hashCode() : 0;
        result = 31 * result + (feedUri != null ? feedUri.hashCode() : 0);
        return result;
    }
}