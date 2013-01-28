package org.ict4htw.atomfeed.client.api.data;

import com.sun.syndication.feed.atom.Entry;

public class Event {
    private Object payload;

	public Event(Entry entry) {
		this.payload = entry; 
	}

	public Object getPayload() {
	   return payload;
	}
}