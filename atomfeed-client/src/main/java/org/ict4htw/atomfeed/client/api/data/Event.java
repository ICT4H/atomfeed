package org.ict4htw.atomfeed.client.api.data;

import com.sun.syndication.feed.atom.Entry;

public class Event {
    private Entry entry;

	public Event(Entry entry) {
		this.entry = entry;
	}

    public String getId() {
        return entry.getId();
    }
}