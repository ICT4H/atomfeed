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
package org.openmrs.module.atomfeed.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.AtomFeedConstants;
import org.openmrs.module.atomfeed.AtomFeedUtil;
import org.openmrs.test.Verifies;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests methods in {@link AtomFeedDownloadServlet} class
 */
@Ignore
public class AtomFeedDownloadServletTest extends BaseModuleWebContextSensitiveTest {
	
	@Before
	public void setDir() {
		Context.getAdministrationService().setGlobalProperty(AtomFeedConstants.GP_CACHE_DIRECTORY, "atomfeedtempdir");
	}
	
	/**
	 * @see AtomFeedDownloadServlet#doHead(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 * @verifies send not modified error if atom feed has not changed
	 */
	@Test
	public void doHead_shouldSendNotModifiedErrorIfAtomFeedHasNotChanged() throws Exception {
		// create servlet and corresponding request and response object to be sent
		AtomFeedDownloadServlet atomFeedDownloadServlet = new AtomFeedDownloadServlet();
		MockHttpServletRequest request = new MockHttpServletRequest("HEAD", "/atomfeed");
		request.setContextPath("/somecontextpath");
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		// intentionally change atom feed in order to not depend from other tests
		AtomFeedUtil.objectCreated(new Encounter());
		
		// read atom feed header specific information from the header file
		String headerFileContent = AtomFeedUtil.readFeedHeaderFile();
		int contentLength = 0;
		String etagToken = "";
		Date lastModified = null;
		if (StringUtils.isNotBlank(headerFileContent)) {
			contentLength = headerFileContent.length()
			        + Integer.valueOf(StringUtils.substringBetween(headerFileContent, "<entriesSize>", "</entriesSize>"));
			etagToken = StringUtils.substringBetween(headerFileContent, "<versionId>", "</versionId>");
			try {
				lastModified = new SimpleDateFormat(AtomFeedUtil.RFC_3339_DATE_FORMAT).parse(StringUtils.substringBetween(
				    headerFileContent, "<updated>", "</updated>"));
			}
			catch (ParseException e) {
				// ignore it here
			}
		}
		// set request headers 
		request.addHeader("If-None-Match", '"' + etagToken + '"');
		request.addHeader("If-Modified-Since", lastModified);
		
		atomFeedDownloadServlet.service(request, response);
		// check response headers
		Assert.assertEquals(contentLength, response.getContentLength());
		Assert.assertEquals("application/atom+xml", response.getContentType());
		Assert.assertEquals(HttpServletResponse.SC_NOT_MODIFIED, response.getStatus());
		Assert.assertNotNull(response.getHeader("Last-Modified"));
	}
	
	/**
	 * @see AtomFeedDownloadServlet#doHead(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 * @verifies send valid headers if atom feed has changed
	 */
	@Test
	public void doHead_shouldSendValidHeadersIfAtomFeedHasChanged() throws Exception {
		// create servlet and corresponding request and response object to be sent
		AtomFeedDownloadServlet atomFeedDownloadServlet = new AtomFeedDownloadServlet();
		MockHttpServletRequest request = new MockHttpServletRequest("HEAD", "/atomfeed");
		request.setContextPath("/somecontextpath");
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		// intentionally change atom feed in order to not depend from other tests
		AtomFeedUtil.objectCreated(new Encounter());
		
		String etagToken = "somevalue";
		Date lastModified = new Date();
		// set request headers 
		request.addHeader("If-None-Match", '"' + etagToken + '"');
		request.addHeader("If-Modified-Since", lastModified);
		
		atomFeedDownloadServlet.service(request, response);
		// check response headers
		Assert.assertNotSame(0, response.getContentLength());
		Assert.assertEquals("application/atom+xml", response.getContentType());
		Assert.assertNotSame(HttpServletResponse.SC_NOT_MODIFIED, response.getStatus());
		Assert.assertNotSame('"' + etagToken + '"', response.getHeader("Etag"));
		Assert.assertNotNull(response.getHeader("Last-Modified"));
	}
	
	/**
	 * @see {@link AtomFeedDownloadServlet#doGet(HttpServletRequest,HttpServletResponse)}
	 */
	@Test
	@Verifies(value = "should exclude entries before the asOfDate value", method = "doGet(HttpServletRequest,HttpServletResponse)")
	public void doGet_shouldExcludeEntriesBeforeTheAsOfDateValue() throws Exception {
		//ensures that at least we have an entry to exclude for testing purposes
		AtomFeedUtil.objectCreated(new Encounter());
		
		AtomFeedDownloadServlet atomFeedDownloadServlet = new AtomFeedDownloadServlet();
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/atomfeed");
		
		Thread.sleep(1000);//wait for at least a second since the dateFormat precision is to seconds
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date asOfDate = new Date();
		request.setParameter("asOfDate", dateFormat.format(asOfDate));
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		AtomFeedUtil.objectCreated(new Encounter());
		AtomFeedUtil.objectCreated(new Concept());
		
		Thread.sleep(2000);//wait for 2 seconds for the feed to get updated		
		atomFeedDownloadServlet.service(request, response);
		
		//only 2 entries added after the asOfDate should have been returned
		Assert.assertEquals(2, StringUtils.countMatches(response.getContentAsString(), "<entry>"));
	}
	
	/**
	 * @see {@link AtomFeedDownloadServlet#doGet(HttpServletRequest,HttpServletResponse)}
	 */
	@Test
	@Verifies(value = "should include all entries if no valid asOfDate is specified", method = "doGet(HttpServletRequest,HttpServletResponse)")
	public void doGet_shouldIncludeAllEntriesIfNoValidAsOfDateIsSpecified() throws Exception {
		//ensures that at least we have an entry to exclude for testing purposes
		AtomFeedUtil.objectCreated(new Encounter());
		
		AtomFeedDownloadServlet atomFeedDownloadServlet = new AtomFeedDownloadServlet();
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/atomfeed");
		
		Thread.sleep(1000);
		
		request.setParameter("asOfDate", "");
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		AtomFeedUtil.objectCreated(new Encounter());
		AtomFeedUtil.objectCreated(new Concept());
		
		Thread.sleep(2000);//wait for 2 seconds for the feed to get updated
		atomFeedDownloadServlet.service(request, response);
		
		//should have returned all entries
		Assert.assertTrue(StringUtils.countMatches(response.getContentAsString(), "<entry>") > 2);
	}
}
