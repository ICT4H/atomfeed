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
        if(entry.getContents().isEmpty()){
            return null;
        }
        String value = ((Content) (entry.getContents().get(0))).getValue();
        return value.replaceFirst("^<!\\[CDATA\\[","").replaceFirst("\\]\\]>$","");
    }
}