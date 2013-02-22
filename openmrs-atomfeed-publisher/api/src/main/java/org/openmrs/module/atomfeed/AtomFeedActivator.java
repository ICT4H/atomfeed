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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.ModuleActivator;


public class AtomFeedActivator implements ModuleActivator {
	
	protected Log log = LogFactory.getLog(getClass());
	
	public void willRefreshContext() {
		log.info("Refreshing Atom Feed Module");
	}
	
	public void contextRefreshed() {
		log.info("Atom Feed Module refreshed");
	}
	
	public void willStart() {
		log.info("Starting Atom Feed Module");
	}
	
	public void started() {
		log.info("Atom Feed Module started");
		
	}
	
	public void willStop() {
		log.info("Stopping Atom Feed Module");
	}
	
	public void stopped() {
		log.info("Atom Feed Module stopped");
	}
	
}
