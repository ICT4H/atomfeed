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

public abstract class AtomFeedConstants {

	/**
	 * Folder where the ever-growing feed file cache will be stored
	 */
	public static final String GP_CACHE_DIRECTORY = "atomfeed.cache_directory";
	public static final String CACHE_DIRECTORY_DEFAULT = "atomfeed";

	public static final String ATOM_FEED_TITLE = "OpenMRS Updates";
	public static final String ATOM_FEED_ID = "urn:uuid:6091b958-88b8-11e1-956f-002713b61fda";
	public static final String ATOM_FEED_LINK_SUFFIX = "atomfeed";
	
	/**
	 * Used by the GeneralEventListener to authenticate a user
	 */
	public static final String GP_MESSAGE_USERNAME = "atomfeed.username";
	
	/**
	 * Used by the GeneralEventListener to authenticate a user
	 */
	public static String GP_MESSAGE_PASSWORD = "atomfeed.password";
	

}
