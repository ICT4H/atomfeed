package org.ict4htw.atomfeed.server.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "event_archive")
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

	public EventArchive() {
		super();
	}
	public EventArchive(String archiveId, String parentArchiveId) {
		this.archiveId = archiveId;
		this.parentId = parentArchiveId;
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

	public void addEvents(List<EventRecord> unarchivedEvents) {
		for (EventRecord record : unarchivedEvents) {
			record.setArchiveId(this.archiveId);
		}
		
	}	
		

}
