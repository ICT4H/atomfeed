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
package org.openmrs.module.feedpublishermodule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.Activator;
import org.openmrs.module.ModuleActivator;

public class FeedPublisherActivator implements ModuleActivator {
	
	private Log log = LogFactory.getLog(this.getClass());

    @Override
    public void willRefreshContext() {
        log.info("Atom Feed Publisher Module : Refreshing Context");
    }

    @Override
    public void contextRefreshed() {
        log.info("Atom Feed Publisher Module : Context Refreshed");
    }

    @Override
    public void willStart() {
        log.info("Atom Feed Publisher Module : Starting Module");
    }

    @Override
    public void started() {
        log.info("Atom Feed Publisher Module : Module Started");
    }

    @Override
    public void willStop() {
        log.info("Atom Feed Publisher Module : Stopping Module");
    }

    @Override
    public void stopped() {
        log.info("Atom Feed Publisher Module : Module Stopped");
    }
}
