package org.ict4h.atomfeed.server.domain;


import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

@Entity
@Table(name = "event_records")
@NamedQueries({
        @NamedQuery(name = EventRecord.FIND_BY_UUID, query = "select e from EventRecord e where e.uuid=:uuid"),
        @NamedQuery(name = EventRecord.TOTAL_COUNT, query = "select count(er) FROM EventRecord er")
})
@XmlRootElement(name = "event", namespace = EventRecord.EVENT_NAMESPACE)
public class EventRecord {
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

    @Column(name = "timestamp", insertable = false, updatable = false)
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


    @Column(name = "date_created", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    private Date dateCreated = new Date();

    @Column(name = "tags")
    @XmlTransient
    private String tags;

    public EventRecord() { }

    public EventRecord(String uuid, String title, String uri, String serializedContents, Date dateCreated, String category) {
        this.uuid = uuid;
        this.title = title;
        this.category = category;
        this.uri = uri;
        this.serializedContents = serializedContents;
        this.dateCreated = dateCreated;
        this.tags = category;
    }

    public EventRecord(String uuid, String title, String uri, String serializedContents, Date dateCreated, String category, String tags) {
        this(uuid, title, uri, serializedContents, dateCreated, category);
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
        return timeStamp != null ? timeStamp : new Date();
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

    @Override
    public String toString() {
        return "EventRecord [id=" + id + ", uuid=" + uuid + ", title=" + title
                + ", timeStamp=" + timeStamp + ", uri=" + uri + ", contents="
                + serializedContents + ", dateCreated=" + dateCreated + ", tags=" + tags + "]";
    }

    public String getCategory() {
        return category;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getTags() {
        return tags;
    }

    //NOTE: intentionally kept private
    private void setTags(String tags) {
        if (!StringUtils.isBlank(tags)) {
            this.tags = tags;
        }
    }
}