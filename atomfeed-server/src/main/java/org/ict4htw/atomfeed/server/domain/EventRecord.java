package org.ict4htw.atomfeed.server.domain;

import java.io.StringWriter;
import java.net.URI;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.ict4htw.atomfeed.server.util.Util;

@Entity
@Table(name = "event_records")
@NamedQueries({
        @NamedQuery(name = EventRecord.FIND_BY_UUID, query = "select e from EventRecord e where e.uuid=:uuid")
})
@XmlRootElement(name = "event", namespace = EventRecord.EVENT_NAMESPACE)
public class EventRecord {

    public static final String FIND_BY_UUID = "find.by.uuid";

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
    private String object;

    public EventRecord() { }

    public EventRecord(String uuid, String title, URI uri, Object eventObject) {
        this(uuid, title, uri, eventObject,new Date());
    }
    public EventRecord(String uuid, String title, URI uri, Object eventObject, Date timeStamp) {
        this.uuid = uuid;
        this.title = title;
        this.uri = uri.toString();
        this.object = Util.stringify(eventObject);
        //note: this is not the date used. the date will be assigned by database
        this.timeStamp = timeStamp;
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
        return "tag.atomfeed.ict4h.org:" + uuid;
    }

    public String getUri() {
        return uri;
    }

    public String getObject() {
        return object;
    }

    public String toXmlString() {
        try {
            JAXBContext context = JAXBContext.newInstance(EventRecord.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(this, stringWriter);

            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
}