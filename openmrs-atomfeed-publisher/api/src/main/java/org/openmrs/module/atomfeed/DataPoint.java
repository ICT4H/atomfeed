/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.atomfeed;

import java.io.Serializable;
import java.util.Date;

import org.openmrs.BaseOpenmrsObject;

/**
 * This represents a single entry in the atom changes feed.
 * 
 * This is an alternative implementation to just storing all changes in an
 * ever-growing file
 */
public class DataPoint extends BaseOpenmrsObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String uuid;
	private Integer creator; // intentionally not a User object so that there
								// are no dependencies
	private Date dateCreated;
	private String atomFeedContent;
	private String objectClass;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getAtomFeedContent() {
		return atomFeedContent;
	}

	public void setAtomFeedContent(String atomFeedContent) {
		this.atomFeedContent = atomFeedContent;
	}

	public String getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(String objectClass) {
		this.objectClass = objectClass;
	}

}