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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.AtomFeedUtil;

/**
 * Download ability for the atom feed
 */
public class AtomFeedDownloadServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(AtomFeedUtil.class);
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 * @should exclude entries before the asOfDate value
	 * @should include all entries if no valid asOfDate is specified
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Date asOfDate = null;
		String asOfDateString = req.getParameter("asOfDate");
		if (!StringUtils.isBlank(asOfDateString)) {
			try {
				asOfDate = new SimpleDateFormat(AtomFeedUtil.RFC_3339_DATE_FORMAT).parse(asOfDateString);
			}
			catch (ParseException ex) {
				try {
					asOfDate = Context.getDateFormat().parse(asOfDateString);
				}
				catch (ParseException e) {
					log.error(asOfDateString + ": for asOfDate parameter is not in the correct date format", ex);
				}
			}
		}
		
		AtomFeedUtil.getAtomFeedStream(resp.getOutputStream(), asOfDate);
	}
	
	/**
	 * This method is called by the servlet container to process a HEAD request made by RSS readers
	 * against the /atomfeed URI
	 * 
	 * @see javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 * @should send not modified error if atom feed has not changed
	 * @should send valid headers if atom feed has changed
	 */
	public void doHead(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// read atomfeed header specific information from the header file
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
		
		// set the content length and type
		resp.setContentLength(contentLength);
		resp.setContentType("application/atom+xml");
		resp.setCharacterEncoding("UTF-8");
		
		// compare previous ETag token with current one
		String previousEtagToken = req.getHeader("If-None-Match");
		Calendar ifModifiedSince = Calendar.getInstance();
		long ifModifiedSinceInMillis = req.getDateHeader("If-Modified-Since");
		ifModifiedSince.setTimeInMillis(ifModifiedSinceInMillis);
		if (((etagToken != null) && (previousEtagToken != null && previousEtagToken.equals('"' + etagToken + '"')))
		        || (ifModifiedSinceInMillis > 0 && ifModifiedSince.getTime().compareTo(lastModified) >= 0)) {
			// send 304 status code that indicates that resource has not been modified
			resp.sendError(HttpServletResponse.SC_NOT_MODIFIED);
			// re-use original last modified time-stamp
			resp.setHeader("Last-Modified", req.getHeader("If-Modified-Since"));
			// no further processing required
			return;
		}
		
		// set header for the next time the client calls
		if (etagToken != null) {
			resp.setHeader("ETag", '"' + etagToken + '"');
			// set the last modified time if it's already specified
			if (lastModified == null) {
				// otherwise set the last modified time to now
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.MILLISECOND, 0);
				lastModified = cal.getTime();
			}
			resp.setDateHeader("Last-Modified", lastModified.getTime());
		}
	}
	
}
