package org.ict4htw.atomfeed.domain;

import org.ict4htw.atomfeed.util.Util;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.StringWriter;
import java.net.URI;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;

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

    @Column(name = "timestamp")
    @XmlTransient
    private Date timeStamp;

    @Column(name = "uri")
    @XmlAttribute
    private String uri;

    @Column(name = "object")
    @XmlElement
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