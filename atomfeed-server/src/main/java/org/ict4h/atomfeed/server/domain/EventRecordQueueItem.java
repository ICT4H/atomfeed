package org.ict4h.atomfeed.server.domain;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.net.URI;
import java.util.Date;

@Entity
@Table(name = "event_records_queue")
@NamedQueries({
        @NamedQuery(name = EventRecordQueueItem.FIND_BY_UUID, query = "select e from EventRecordQueue e where e.uuid=:uuid"),
        @NamedQuery(name = EventRecordQueueItem.TOTAL_COUNT, query = "select count(er) FROM EventRecordQueue er")
})
@XmlRootElement(name = "event", namespace = EventRecordQueueItem.EVENT_NAMESPACE)
public class EventRecordQueueItem {
    public static final String FIND_BY_UUID = "find.by.uuid";
    public static final String TOTAL_COUNT = "event_records.total_count";

    public static final String EVENT_NAMESPACE = "http://schemas.atomfeed.ict4h.org/events";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlTransient
    private Integer id;

    @Column(name = "uuid")
    @XmlTransient
    private String uuid;

    @Column(name = "title")
    @XmlTransient
    private String title;

    @Column(name = "timestamp", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    private Date timeStamp;

    @Column(name = "uri")
    @XmlAttribute
    private String uri;

    @Column(name = "object")
    @XmlElement
    private String serializedContents;

    @Column(name = "category")
    @XmlTransient
    private String category;

    @Column(name="tags")
    @XmlTransient
    private String tags;


    public EventRecordQueueItem() { }

    public EventRecordQueueItem(String uuid, String title, URI uri, String serializedContents, String category) {
        this.uuid = uuid;
        this.title = title;
        this.category = category;
        this.uri = uri == null ? null : uri.toString();
        this.serializedContents = serializedContents;
        this.timeStamp = new Date();
        this.tags = category;
    }

    public EventRecordQueueItem(String uuid, String title, URI uri, String serializedContents, Date timeStamp, String category) {
        this(uuid, title, uri, serializedContents, category);
        this.timeStamp = (timeStamp != null) ? timeStamp : new Date();
    }

    public EventRecordQueueItem(String uuid, String title, URI uri, String serializedContents, Date timeStamp, String category, String tags) {
        this(uuid, title, uri, serializedContents, timeStamp, category);
        setTags(tags);
    }

    public Integer getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getTagUri() {
        return "tag:atomfeed.ict4h.org:" + uuid;
    }

    public String getUri() {
        return uri;
    }

    public String getContents() {
        return serializedContents;
    }

    public String getCategory() {
        return category;
    }

    public String getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "EventRecordQueueItem{" +
                "id=" + id + ", uuid='" + uuid + ", title='" + title +
                ", timeStamp=" + timeStamp + ", uri='" + uri +
                ", serializedContents='" + serializedContents +
                ", category='" + category +
                ", tags='" + tags + '}';
    }

    //NOTE: intentionally kept private
    private void setTags(String tags) {
        if (StringUtils.isBlank(tags)) return;
        String normalizedTagString = StringUtils.normalizeSpace(tags);
        if (!StringUtils.isBlank(normalizedTagString)) {
            this.tags = this.category + "," + normalizedTagString;
        }
    }
}