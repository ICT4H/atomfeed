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

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.OpenmrsData;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * 
 *
 */
 @Ignore
public class AtomFeedUtilTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setDir() {
		Context.getAdministrationService().setGlobalProperty(AtomFeedConstants.GP_CACHE_DIRECTORY, "atomfeedtempdir");
	}
	
	/**
	 * @see AtomFeedUtil#writeToFeed(String,OpenmrsObject)
	 * @verifies initialize header file
	 */
	@Test
	public void writeToFeed_shouldInitializeHeaderFile() throws Exception {
		AtomFeedUtil.writeToFeed("test", new Patient());
	}
	
	/**
	 * @see AtomFeedUtil#writeToFeed(String,OpenmrsObject)
	 * @verifies prepend valid entry to entries file
	 */
	@Test
	public void writeToFeed_shouldPrependValidEntryToEntriesFile() throws Exception {
		OpenmrsData target = new Encounter();
		// creating almost exact copy of entry to be write to feed (the
		// only difference will be with updated tag value)
		String entry = AtomFeedUtil.getEntry("test1", target);
		int entryLength = entry.length();
		// obtaining feed file object to work with
		File feedFile = AtomFeedUtil.getFeedEntriesFile();
		int prevSize = 0;
		if (feedFile.exists()) {
			prevSize = FileUtils.readFileToString(feedFile).length();
		}
		AtomFeedUtil.writeToFeed("test1", new Encounter());
		String feedEntries = FileUtils.readFileToString(feedFile);
		// asserting by checking if entries file size has grown 
		// for just written atom feed entry
		Assert.assertEquals(feedEntries.length(), entryLength + prevSize + String.format("%n").length() /* consider with new line separator added to the end of each feed */);
	}
	
	/**
	 * @see AtomFeedUtil#getUpdatedValue(OpenmrsObject)
	 */
	@Test
	public void getUpdatedValue_shouldReturnDateChangedValueIfPresent() throws Exception {
		Date dateChanged = new Date();
		Patient patient = new Patient();
		patient.setDateChanged(dateChanged);
		
		Assert.assertEquals(dateChanged, AtomFeedUtil.getUpdatedValue(patient));
	}
	
	/**
	 * @see AtomFeedUtil#getUpdatedValue(OpenmrsObject)
	 */
	@Test
	public void getUpdatedValue_shouldReturnDateCreatedValueIfPresentAndDateChangedIsNullOrNotPresent() throws Exception {
		Date dateCreated = new Date();
		Patient patient = new Patient();
		patient.setDateCreated(dateCreated);
		
		Assert.assertEquals(dateCreated, AtomFeedUtil.getUpdatedValue(patient));
	}
	
	/**
	 * @see AtomFeedUtil#getUpdatedValue(OpenmrsObject)
	 */
	@Test
	public void getUpdatedValue_shouldNeverReturnNullEvenIfBothDateChangedAndDateCreatedAreNull() throws Exception {
		Patient patient = new Patient();
		Assert.assertNotNull(AtomFeedUtil.getUpdatedValue(patient));
	}
	
	/**
	 * @see AtomFeedUtil#getAuthor(OpenmrsObject)
	 */
	@Test
	public void getAuthor_shouldReturnChangedByValueIfPresent() throws Exception {
		final String username = "test";
		
		Person person = new Person();
		person.addName(new PersonName("mr", "tester", "man"));
		User changedBy = new User(person);
		changedBy.setUsername(username);
		
		Concept concept = new Concept();
		concept.setChangedBy(changedBy);
		
		Assert.assertEquals(changedBy.getPersonName().getFullName() + " (" + username + ")", AtomFeedUtil.getAuthor(concept));
	}
	
	/**
	 * @see AtomFeedUtil#getAuthor(OpenmrsObject)
	 */
	@Test
	public void getAuthor_shouldReturnCreatorIfPresentAndChangedByIsNullOrNotPresent() throws Exception {
		final String username = "test";
		
		Person person = new Person();
		person.addName(new PersonName("mr", "tester", "man"));
		User creator = new User(person);
		creator.setUsername(username);
		
		Concept concept = new Concept();
		concept.setCreator(creator);
		
		Assert.assertEquals(creator.getPersonName().getFullName() + " (" + username + ")", AtomFeedUtil.getAuthor(concept));
	}
	
	/**
	 * @see AtomFeedUtil#getAtomFeedStream(java.io.OutputStream, java.util.Date)
	 * @verifies download full stream with null date
	 */
	@Test
	public void getAtomFeedStream_shouldDownloadFullStreamWithNullDate() throws Exception {
		MockHttpServletResponse response = new MockHttpServletResponse();
		// if atom feed file exists, just get rid of it 
		File feedFile = AtomFeedUtil.getFeedEntriesFile();
		if (feedFile.exists()) {
			feedFile.delete();
		}
		// write couple of entries to atom feed 
		AtomFeedUtil.writeToFeed("test1", new Encounter());
		AtomFeedUtil.writeToFeed("test2", new Patient());
		
		AtomFeedUtil.getAtomFeedStream(response.getOutputStream(), null);
		
		// get response content to use it when asserting
		String responseContent = response.getContentAsString();
		
		// test if response contains header file content
		String atomHeader = FileUtils.readFileToString(AtomFeedUtil.getFeedHeaderFile());
		// truncate "</feed>" from the atom header string
		if (StringUtils.isNotBlank(atomHeader)) {
			atomHeader = StringUtils.substringBeforeLast(atomHeader, "</feed>");
		}
		Assert.assertTrue(StringUtils.contains(responseContent, atomHeader));
		
		// test that response content also contains both entries
		Assert.assertTrue(StringUtils.contains(responseContent, "<action>test1</action>"));
		Assert.assertTrue(StringUtils.contains(responseContent, "<action>test2</action>"));
		
		// test that response content also contains closing tag </feed>
		Assert.assertTrue(StringUtils.endsWith(responseContent, "</feed>"));
	}
	
	/**
	 * @see AtomFeedUtil#getAtomFeedStream(java.io.OutputStream, java.util.Date)
	 * @verifies download partial stream by given date
	 */
	@Test
	public void getAtomFeedStream_shouldDownloadPartialStreamByGivenDate() throws Exception {
		MockHttpServletResponse response = new MockHttpServletResponse();
		// if atom feed file exists, just get rid of it 
		File feedFile = AtomFeedUtil.getFeedEntriesFile();
		if (feedFile.exists()) {
			feedFile.delete();
		}
		// write couple of entries to atom feed 
		AtomFeedUtil.writeToFeed("test1", new Encounter());
		AtomFeedUtil.writeToFeed("test2", new Patient());
		// do some sleep to have asOfDate parameter for filtering entries
		Calendar asOfDate = Calendar.getInstance();
		Thread.sleep(1500);
		// add another entries (ones which will be filtered to stream)
		AtomFeedUtil.writeToFeed("test3", new Encounter());
		AtomFeedUtil.writeToFeed("test4", new Patient());
		
		AtomFeedUtil.getAtomFeedStream(response.getOutputStream(), asOfDate.getTime());
		
		// get response content to use it when asserting
		String responseContent = response.getContentAsString();
		
		// test if response contains header file content
		String atomHeader = FileUtils.readFileToString(AtomFeedUtil.getFeedHeaderFile());
		// truncate "</feed>" from the atom header string
		if (StringUtils.isNotBlank(atomHeader)) {
			atomHeader = StringUtils.substringBeforeLast(atomHeader, "</feed>");
		}
		Assert.assertTrue(StringUtils.contains(responseContent, atomHeader));
		
		// test that response content also contains both entries, added after sleep
		Assert.assertTrue(StringUtils.contains(responseContent, "<action>test3</action>"));
		Assert.assertTrue(StringUtils.contains(responseContent, "<action>test4</action>"));
		
		// test that response content also contains both entries, added after sleep
		Assert.assertFalse(StringUtils.contains(responseContent, "<action>test1</action>"));
		Assert.assertFalse(StringUtils.contains(responseContent, "<action>test2</action>"));
		
		// test that response content also contains closing tag </feed>
		Assert.assertTrue(StringUtils.endsWith(responseContent, "</feed>"));
		
	}
	
	/**
	 * @see AtomFeedUtil#getAtomFeedStream(java.io.OutputStream, java.util.Date)
	 * @verifies stream multiline entry
	 */
	@Test
	public void getAtomFeedStream_shouldStreamMultiLineEntry() throws Exception {
		MockHttpServletResponse response = new MockHttpServletResponse();
		// if atom feed file exists, just get rid of it 
		File feedFile = AtomFeedUtil.getFeedEntriesFile();
		if (feedFile.exists()) {
			feedFile.delete();
		}
		// do some sleep to have asOfDate parameter for filtering entries
		Calendar asOfDate = Calendar.getInstance();
		Thread.sleep(1500);
		// write couple of entries to atom feed 
		AtomFeedUtil.writeToFeed("test1\ntest2", new Encounter());
		AtomFeedUtil.writeToFeed("test3", new Encounter());
		AtomFeedUtil.writeToFeed("test4", new Patient());
		
		AtomFeedUtil.getAtomFeedStream(response.getOutputStream(), asOfDate.getTime());
		
		// get response content to use it when asserting
		String responseContent = response.getContentAsString();
		
		// test if response contains header file content
		String atomHeader = FileUtils.readFileToString(AtomFeedUtil.getFeedHeaderFile());
		// truncate "</feed>" from the atom header string
		if (StringUtils.isNotBlank(atomHeader)) {
			atomHeader = StringUtils.substringBeforeLast(atomHeader, "</feed>");
		}
		Assert.assertTrue(StringUtils.contains(responseContent, atomHeader));
		
		// test that response content also contains entries
		Assert.assertTrue(StringUtils.contains(responseContent, "test1\ntest2"));
		Assert.assertTrue(StringUtils.contains(responseContent, "<action>test3</action>"));
		Assert.assertTrue(StringUtils.contains(responseContent, "<action>test4</action>"));
		
		// test that response content also contains closing tag </feed>
		Assert.assertTrue(StringUtils.endsWith(responseContent, "</feed>"));
		
	}
	
	/**
	 * @see {@link AtomFeedUtil#getViewUrl(OpenmrsObject)}
	 */
	@Test
	@Verifies(value = "should return url for Patient object", method = "getViewUrl(OpenmrsObject)")
	public void getViewUrl_shouldReturnUrlForPatientObject() throws Exception {
		String patientUuid = "5946f880-b197-400b-9caa-a3c661d23041";
		Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
		
		Assert.assertEquals("NEED-TO-CONFIGURE/ws/rest/v1/person/" + patientUuid, AtomFeedUtil.getViewUrl(patient));
	}
	
	/**
	 * @see AtomFeedUtil#dateToRFC3339(Date)
	 * @verifies not fail given a null date
	 */
	@Test
	public void dateToRFC3339_shouldNotFailGivenANullDate() throws Exception {
		Date nDate = null;
		Assert.assertNull(AtomFeedUtil.dateToRFC3339(nDate));
	}
	
	/**
	 * @see AtomFeedUtil#dateToRFC3339(Date)
	 * @verifies convert date to rfc
	 */
	@Test
	public void dateToRFC3339_shouldConvertDateToRfc() throws Exception {
		TimeZone tz = TimeZone.getDefault();
		System.out.println("timezone: " + tz.getDisplayName());
		try {
			TimeZone.setDefault(TimeZone.getTimeZone("Europe/Helsinki"));
			String expectedOutput = "2012-05-08T22:39:54+03:00";
			Date date = new Date(1336505994083l);
			Assert.assertEquals(expectedOutput, AtomFeedUtil.dateToRFC3339(date));
		}
		finally {
			TimeZone.setDefault(tz); // reset back to what it was
		}
	}
	
	/**
	 * @see {@link AtomFeedUtil#getAuthor(OpenmrsObject)}
	 */
	@Test
	@Verifies(value = "should return the system id of the user if username is blank", method = "getAuthor(OpenmrsObject)")
	public void getAuthor_shouldReturnTheSystemIdOfTheUserIfUsernameIsBlank() throws Exception {
		final String systemId = "random Id";
		
		Person person = new Person();
		person.addName(new PersonName("mr", "tester", "man"));
		User changedBy = new User(person);
		changedBy.setSystemId(systemId);
		
		Concept concept = new Concept();
		concept.setChangedBy(changedBy);
		
		Assert.assertEquals(changedBy.getPersonName().getFullName() + " (" + systemId + ")", AtomFeedUtil.getAuthor(concept));
	}
	
	/**
	 * @see {@link AtomFeedUtil#getAuthor(OpenmrsObject)}
	 */
	@Test
	@Verifies(value = "should return unknown if changedBy and creator are not set", method = "getAuthor(OpenmrsObject)")
	public void getAuthor_shouldReturnUnknownIfChangedByAndCreatorAreNotSet() throws Exception {
		Concept concept = new Concept();
		Assert.assertEquals("Unknown", AtomFeedUtil.getAuthor(concept));
	}
}
