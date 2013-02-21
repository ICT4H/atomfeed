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
package org.openmrs.module.atomfeedpublisher;


import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.ModuleActivator;


public class AtomFeedPublisherActivator implements ModuleActivator {
	
	protected Log log = LogFactory.getLog(getClass());

	public void willRefreshContext() {
		log.info("Refreshing AtomFeed Publisher Module");
	}

	public void contextRefreshed() {
		log.info("AtomFeed Publisher Module refreshed");
	}

	public void willStart() {
		log.info("Starting AtomFeed Publisher Module");
	}

	public void started() {
		log.info("AtomFeed Publisher Module started");
	}

	public void willStop() {
		log.info("Stopping AtomFeed Publisher Module");
	}
	
	public void stopped() {
		log.info("AtomFeed Publisher Module stopped");
	}
		
}
