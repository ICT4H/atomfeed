package org.ict4htw.atomfeed.server.domain;

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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "event_archive")
@NamedQueries({
        @NamedQuery(name = EventRecord.FIND_BY_UUID, query = "select e from EventRecord e where e.uuid=:uuid")
})
@XmlRootElement(name = "event", namespace = EventRecord.EVENT_NAMESPACE)
public class EventArchive {

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlTransient
    private Integer id;
	
	@Column(name = "archive_id")
    @XmlElement
	private String archiveId;
	
	@Column(name = "parent_id")
    @XmlElement
	private String parentId;
	
	@Column(name = "timestamp", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    private Date timeStamp;

	public EventArchive(String archiveId) {
		this.archiveId = archiveId;
	}

	public String getArchiveId() {
		return archiveId;
	}

	public String getParentId() {
		return parentId;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public Integer getId() {
		return id;
	}		

}
