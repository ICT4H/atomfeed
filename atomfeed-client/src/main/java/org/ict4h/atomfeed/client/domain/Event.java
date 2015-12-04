package org.ict4h.atomfeed.client.domain;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {

    private List categories = new ArrayList();
    private String id;
    private String content;
    private String feedUri;
    private Date updatedDate;
    private String title;

    private Date dateCreated;

    public Event(Entry entry) {
        this(entry, null);
	}

    public Event(String id, String content) {
        this(id, content, null, null, null);
    }

    public Event(String id, String content, String title) {
        this(id, content, title, null, null);
    }

    public Event(String id, String content, String title, String feedUri, Date updatedDate) {
        this.id = id;
        this.content = content;
        this.title = title;
        this.feedUri = feedUri;
        this.updatedDate = updatedDate;
    }

    public Event(Entry entry ,String feedUri) {
        this(entry.getId(), extractContent(entry), entry.getTitle(), feedUri, entry.getUpdated());
        this.categories = entry.getCategories();
        this.dateCreated = entry.getCreated();
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

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public List getCategories() {
        return categories;
    }

    public Date getDateCreated() {
        return dateCreated != null ? dateCreated : updatedDate;
    }
}