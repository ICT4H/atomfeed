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

import java.util.Arrays;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.event.SubscribableEventListener;
import org.openmrs.module.atomfeed.api.AtomFeedService;

/**
 * This class is registered to the {@link Event} in the moduleApplicationContext spring file. It
 * subscribes to all relevant objects and all relevant actions.
 */
public class GeneralEventListener implements SubscribableEventListener {
	
	private static Log log = LogFactory.getLog(GeneralEventListener.class);
	
	@Override
	public void onMessage(Message msgParam) {
		Context.openSession();
		String username = Context.getAdministrationService().getGlobalProperty(AtomFeedConstants.GP_MESSAGE_USERNAME, "");
		String password = Context.getAdministrationService().getGlobalProperty(AtomFeedConstants.GP_MESSAGE_PASSWORD, "");
		if (!username.isEmpty())
			Context.authenticate(username, password);
		
		MapMessage msg = (MapMessage) msgParam;
		String action;
		String uuid;
		String classname;
		try {
			action = msg.getString("action");
			classname = msg.getString("classname");
			uuid = msg.getString("uuid");
		}
		catch (JMSException e) {
			log.error("unable to get strings off of the MapMessage", e);
			
			// fail hard here			
			return;
		}
		
		log.error("action: " + action + " object : " + classname + " uuid: " + uuid);
		
		OpenmrsObject openmrsObject = Context.getService(AtomFeedService.class).getObjectByUuid(classname, uuid);
		
		/*
		 * intentionally separating the methods here so that AtomFeedUtil
		 * doesn't have a dependency on Event.Action
		 */
		if (action.equals(Event.Action.CREATED.name())) {
			AtomFeedUtil.objectCreated(openmrsObject);
		} else if (action.equals(Event.Action.UPDATED.name())) {
			AtomFeedUtil.objectUpdated(openmrsObject);
		} else if (action.equals(Event.Action.VOIDED.name())) {
			AtomFeedUtil.objectVoided(openmrsObject);
		} else if (action.equals(Event.Action.PURGED.name())) {
			AtomFeedUtil.objectDeleted(openmrsObject);
		}
		
		Context.closeSession();
	}
	
	@Override
	public List<Class<? extends OpenmrsObject>> subscribeToObjects() {
		// admittedly a very strange way to use a convenience method, but java
		// compilation wouldn't occur without this extra line
		// TODO get this list from a GP
		Object classes = Arrays.asList(Patient.class, Concept.class, Encounter.class, Obs.class);
		return (List<Class<? extends OpenmrsObject>>) classes;
	}
	
	@Override
	public List<String> subscribeToActions() {
		return Arrays.asList(Event.Action.CREATED.name(), Event.Action.UPDATED.name(), Event.Action.VOIDED.name(), Event.Action.PURGED.name());
	}
	
}
