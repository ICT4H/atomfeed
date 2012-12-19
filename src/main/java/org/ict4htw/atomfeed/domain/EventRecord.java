package org.ict4htw.atomfeed.domain;

import org.ict4htw.atomfeed.util.Util;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.net.URI;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;

@Entity
@Table(name = "event_records")
@NamedQuery(name = EventRecord.FIND_BY_UUID, query = "select e from EventRecord e where e.uuid=:uuid")
public class EventRecord {

    public static final String FIND_BY_UUID = "find.by.uuid";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "title")
    private String title;

    @Column(name = "timestamp")
    private Date timeStamp;

    @Column(name = "uri")
    private String uri;

    @Column(name = "object")
    private String object;

    public EventRecord() { }

    public EventRecord(String uuid, String title, DateTime timeStamp, URI uri, Object eventObject) {
        this.uuid = uuid;
        this.title = title;
        this.timeStamp = new Date(timeStamp.getMillis());
        this.uri = uri.toString();
        this.object = Util.stringify(eventObject);
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

    public String getUri() {
        return uri;
    }

    public String getObject() {
        return object;
    }
}