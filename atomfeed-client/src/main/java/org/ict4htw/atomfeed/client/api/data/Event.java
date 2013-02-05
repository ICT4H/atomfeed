package org.ict4htw.atomfeed.client.api.data;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;

public class Event {

    private Entry entry;


	public Event(Entry entry) {
		this.entry = entry;
	}

    /**
     * returns the id of Entry
     * @return
     */
    public String getId() {
        return entry.getId();
    }

    /**
     *
     * reads the list of contents of entry and returns the first content's value as string
     * for now as a contract we have only one content in the list.
     *
     * @return
     */
    public String getContent(){
        Content content = (Content) entry.getContents().get(0);
        return entry.getContents().size()!=0? content.getValue() :null;
    }
}