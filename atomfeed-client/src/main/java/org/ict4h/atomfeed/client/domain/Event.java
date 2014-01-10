package org.ict4h.atomfeed.client.domain;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;

public class Event {

    private String id;
    private String content;
    private String feedUri;
    private String title;

    public Event(Entry entry) {
        this(entry, null);
	}

    public Event(String id, String content) {
        this(id, content, null, null);
    }

    public Event(String id, String content, String title) {
        this(id, content, title, null);
    }

    public Event(String id, String content, String title, String feedUri) {
        this.id = id;
        this.content = content;
        this.title = title;
        this.feedUri = feedUri;
    }

    public Event(Entry entry ,String feedUri) {
        this(entry.getId(), extractContent(entry), entry.getTitle(), feedUri);
    }

    /**
     * reads the list of contents of entry and returns the first content's value as string
     * for now as a contract we have only one content in the list.
     *
     * @return
     */
    private static String extractContent(Entry entry) {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}