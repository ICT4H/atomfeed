package org.ict4h.atomfeed.client.domain;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;

public class Event {

    private String id;
    private String content;
    private String feedUri;

    public Event(Entry entry) {
        this.id = entry.getId();
		this.content = extractContent(entry);
	}

    public Event(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public Event(Entry entry ,String feedUri) {
        this(entry);
        this.feedUri = feedUri;
    }

    /**
     * reads the list of contents of entry and returns the first content's value as string
     * for now as a contract we have only one content in the list.
     *
     * @return
     */
    private String extractContent(Entry entry) {
        if(entry.getContents().isEmpty()){
            return null;
        }

        String value = ((Content) (entry.getContents().get(0))).getValue();
        return value.replaceFirst("^<!\\[CDATA\\[","").replaceFirst("\\]\\]>$","");
    }

    /**
     * It does remove the CDATA part of XML
     * @return
     */
    public String getContent(){
        return this.content;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public String getFeedUri() {
        return feedUri;
    }
}