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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.OpenmrsObject;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.GlobalPropertyListener;
import org.openmrs.api.context.Context;
import org.openmrs.util.HandlerUtil;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.util.ReflectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Utility methods for the atom feed module
 */
public abstract class AtomFeedUtil implements GlobalPropertyListener {
	
	private static Log log = LogFactory.getLog(AtomFeedUtil.class);
	
	private static String serverUrl;
	
	private static String restWebServiceUrlGPName;
	
	public static Class<?> resourceClass = null;
	
	static {
		try {
			restWebServiceUrlGPName = ReflectionUtils
			        .findField(Context.loadClass("org.openmrs.module.webservices.rest.web.RestConstants"),
			            "URI_PREFIX_GLOBAL_PROPERTY_NAME").get(null).toString();
		}
		catch (ClassNotFoundException e) {
			log.error("Failed to load class: org.openmrs.module.webservices.rest.web.RestConstants");
		}
		catch (IllegalArgumentException e) {
			log.error("Failed to get the value of the 'URI_PREFIX_GLOBAL_PROPERTY_NAME' field for class: org.openmrs.module.webservices.rest.web.RestConstants");
		}
		catch (IllegalAccessException e) {
			log.error("Failed to access 'URI_PREFIX_GLOBAL_PROPERTY_NAME' field for class: org.openmrs.module.webservices.rest.web.RestConstants");
		}
	}
	
	/**
	 * The date and time format according to <a href="http://www.ietf.org/rfc/rfc3339.txt">RFC
	 * 3339</a>
	 */
	public static final String RFC_3339_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	
	/**
	 * @param openmrsObject
	 */
	public static void objectCreated(OpenmrsObject openmrsObject) {
		writeToFeed("create", openmrsObject);
	}
	
	public static void objectUpdated(OpenmrsObject openmrsObject) {
		writeToFeed("update", openmrsObject);
	}
	
	public static void objectDeleted(OpenmrsObject openmrsObject) {
		writeToFeed("delete", openmrsObject);
	}
	
	public static void objectVoided(OpenmrsObject openmrsObject) {
		writeToFeed("void", openmrsObject);
	}
	
	/**
	 * This method writes the atom feed data to the given stream. <br/>
	 * The given stream is not closed
	 * 
	 * @param stream an open outputstream that will be written to
	 * @param asOfDate if not null, limits the entries to only ones updated after this date
	 * @should download full stream with null date
	 * @should download partial stream by given date
	 * @should stream multiline entry
	 */
	public static void getAtomFeedStream(OutputStream stream, Date asOfDate) {
		OutputStream out = new BufferedOutputStream(stream);
		
		File atomheaderfile = getFeedHeaderFile();
		if (atomheaderfile.exists()) {
			try {
				// stream the atom header to output
				String atomHeader = FileUtils.readFileToString(atomheaderfile);
				// truncate "</feed>" from the atom header string
				if (StringUtils.isNotBlank(atomHeader)) {
					atomHeader = StringUtils.substringBeforeLast(atomHeader, "</feed>");
				}
				// write part of the header to the stream 
				out.write(atomHeader.getBytes());
				
				// then stream the entries to the output
				File atomfile = getFeedEntriesFile();
				
				// if the date filtering parameter is passed in
				// we need to limit the entries to only ones, which were 
				// updated after this date
				if (asOfDate != null) {
					String entry;
					BufferedReader br = new BufferedReader(new FileReader(atomfile));
					while ((entry = br.readLine()) != null) {
						// if current entry has a new line then handle it gracefully
						while (!StringUtils.endsWith(entry, "</entry>")) {
							String newLine = br.readLine();
							// if end of file is reached and new line does not contain new entry
							if (newLine != null && !StringUtils.contains(newLine, "<entry>")) {
								entry = entry.concat("\n").concat(newLine);
							} else {
								// otherwise an invalid entry is found, terminate processing
								throw new Exception("Invalid atom feed entry. No end tag </entry> found.");
							}
						}
						Date updated = new SimpleDateFormat(RFC_3339_DATE_FORMAT).parse(StringUtils.substringBetween(entry, "<updated>", "</updated>"));
						if (updated.compareTo(asOfDate) > -1) {
							// write entry to the stream 
							entry = entry.concat("\n");
							out.write(entry.getBytes());
						} else {
							// if entry with updatedDate lower that given one is reached
							// we need to stop filtering
							break;
						}
					}
				} else {
					// bulk write all entries to the stream 
					out.write(FileUtils.readFileToByteArray(atomfile));
				}
				
				// write the "</feed>" element that isn't in the entries file (and was
				// in the header file)
				out.write("</feed>".getBytes());
				out.flush();
			}
			catch (Exception e) {
				log.error("Unable to stream atom header file and/or entries file, because of error: ", e);
			}
		}
	}
	
	/**
	 * Does the work of writing the given object update to the feed file
	 * 
	 * @param action what happened
	 * @param object the object that was changed
	 * @should initialize header file
	 * @should change updated time in existing header file
	 * @should prepend valid entry to entries file
	 */
	protected static synchronized void writeToFeed(String action, OpenmrsObject object) {
		
		// get handle on file for entries
		File atomfile = getFeedEntriesFile();
		
		// write action/change to file
		String entry = getEntry(action, object);
		// prepend given entry string to the beginning of atom file
		BufferedWriter out = null;
		FileInputStream source = null;
		FileOutputStream destination = null;
		File temporaryAtomFile = null;
		try {
			
			// create hidden temporary atom file within atom feeds  
			// directory to write new feed entry to
			if (!atomfile.exists()) {
				out = new BufferedWriter(new FileWriter(atomfile, Boolean.TRUE));
				out.write(entry);
				out.newLine();
			} else {
				temporaryAtomFile = new File(atomfile.getParent(), ".".concat(atomfile.getName()));
				out = new BufferedWriter(new FileWriter(temporaryAtomFile, Boolean.TRUE));
				out.write(entry);
				out.newLine();
				// copy existing atom feed entries from current atom feed file
				// to temporary file
				BufferedReader br = new BufferedReader(new FileReader(atomfile));
				while ((entry = br.readLine()) != null) {
					out.write(entry);
					out.newLine();
				}
				out.flush();
				IOUtils.closeQuietly(out);
				
				// get rid of old feed entries file by swap it's content with new one
				source = new FileInputStream(temporaryAtomFile);
				destination = new FileOutputStream(atomfile);
				IOUtils.copy(source, destination);
			}
		}
		catch (IOException e) {
			log.error("Unable to write entry to the feed entries file, because of error:", e);
		}
		finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(source);
			IOUtils.closeQuietly(destination);
			FileUtils.deleteQuietly(temporaryAtomFile);
		}
		
		// get handle on header file
		File atomheaderfile = getFeedHeaderFile();
		
		// re-/creates the header and/or updates the "last updated" time to now
		// the entire file doesn't always need rewritten for every entry,
		// but its not that large, so we're not losing many cpu cycles
		// TODO: look into only changing the "updated" element to reduce cpu
		// usage -- ATOM-4        
		updateFeedFileHeader(atomheaderfile, atomfile.length());
	}
	
	/**
	 * Converts the given object to an xml entry
	 * 
	 * @param action what is happenening
	 * @param object the object being changed
	 * @return atom feed xml entry string
	 * @should return valid entry xml data
	 */
	protected static String getEntry(String action, OpenmrsObject object) {
		try {
			// We need a Document
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			// //////////////////////
			// Creating the XML tree
			
			// create the root element and add it to the document
			Element root = doc.createElement("entry");
			doc.appendChild(root);
			
			// the title element is REQUIRED
			// create title element, add object class, and add to root
			Element title = doc.createElement("title");
			Text titleText = doc.createTextNode(action + ":" + object.getClass().getName());
			title.appendChild(titleText);
			root.appendChild(title);
			
			// create link to view object details
			Element link = doc.createElement("link");
			link.setAttribute("href", AtomFeedUtil.getViewUrl(object));
			root.appendChild(link);
			
			// the id element is REQUIRED
			// create id element
			Element id = doc.createElement("id");
			Text idText = doc.createTextNode("urn:uuid:" + object.getUuid());
			id.appendChild(idText);
			root.appendChild(id);
			
			// the updated element is REQUIRED
			// create updated element, set current date
			Element updated = doc.createElement("updated");
			// TODO: try to discover dateChanged/dateCreated from object -- ATOM-2
			// instead?
			Text updatedText = doc.createTextNode(dateToRFC3339(getUpdatedValue(object)));
			updated.appendChild(updatedText);
			root.appendChild(updated);
			
			// the author element is REQUIRED
			// add author element, find creator
			Element author = doc.createElement("author");
			Element name = doc.createElement("name");
			Text nameText = doc.createTextNode(getAuthor(object));
			
			name.appendChild(nameText);
			author.appendChild(name);
			root.appendChild(author);
			
			// the summary element is REQUIRED
			// add a summary
			Element summary = doc.createElement("summary");
			Text summaryText = doc.createTextNode(object.getClass().getSimpleName() + " -- " + action);
			summary.appendChild(summaryText);
			root.appendChild(summary);
			
			Element classname = doc.createElement("classname");
			Text classnameText = doc.createTextNode(object.getClass().getName());
			classname.appendChild(classnameText);
			root.appendChild(classname);
			
			Element actionElement = doc.createElement("action");
			Text actionText = doc.createTextNode(action);
			actionElement.appendChild(actionText);
			root.appendChild(actionElement);
			
			/*
			 * Print the xml to the string
			 */

			// set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "no");
			
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			
			return sw.toString();
		}
		catch (Exception e) {
			log.error("unable to create entry string for: " + object);
			return "";
		}
	}
	
	/**
	 * Returns the url for the atomfeed entry element "link".
	 * 
	 * @param object the object being updated/changed
	 * @return a url (to a web service) that will allow the atom feed reader to fetch the entire
	 *         object
	 * @should not fail if no ws url is defined
	 * @should return url for Patient object
	 * @should return url for non core openmrs object
	 */
	protected static String getViewUrl(OpenmrsObject object) {
		//if the action was purged, won't this be null
		if (object != null) {
			try {
				if (resourceClass == null)
					resourceClass = Context.loadClass("org.openmrs.module.webservices.rest.web.resource.api.Resource");
				
				//Use reflection since the module api jar doesn't contain web classes
				Object objectResource = HandlerUtil.getPreferredHandler(resourceClass, object.getClass());
				return MethodUtils.invokeMethod(objectResource, "getUri", object).toString();
			}
			catch (ClassNotFoundException e) {
				log.error("Failed to log class: org.openmrs.module.webservices.rest.web.resource.api.Resource", e);
			}
			catch (NoSuchMethodException e) {
				log.error("Failed to find method getUri(Object) in " + object.getClass().getSimpleName()
				        + " resource class: ", e);
			}
			catch (IllegalAccessException e) {
				log.error("Failed to access method getUri(Object) in " + object.getClass().getSimpleName()
				        + " resource class", e);
			}
			catch (InvocationTargetException e) {
				log.error("Failed to invoke method getUri(Object) in " + object.getClass().getSimpleName()
				        + " resource class:", e);
			}
		}
		
		return "";
	}
	
	/**
	 * @return File for just the header of the atomfeed <br/>
	 * <br/>
	 *         <b>NOTE:</b> It was intentionally made as protected to be available in tests
	 * @see #getFeedEntriesFile()
	 * @see #readFeedHeaderFile()
	 */
	protected static File getFeedHeaderFile() {
		String folderName = Context.getAdministrationService().getGlobalProperty(AtomFeedConstants.GP_CACHE_DIRECTORY,
		    AtomFeedConstants.CACHE_DIRECTORY_DEFAULT);
		File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		
		if (!dir.exists())
			dir.mkdirs();
		
		return new File(dir, "atomfeedheader");
	}
	
	/**
	 * Reads content of ATOM header file if this exists
	 * 
	 * @return string content of just the header of the atomfeed
	 */
	public static String readFeedHeaderFile() {
		File headerFile = getFeedHeaderFile();
		if (headerFile.exists()) {
			try {
				return FileUtils.readFileToString(headerFile);
			}
			catch (IOException e) {
				log.error("Unable to read content of feed header file, because of error:", e);
			}
		}
		return "";
	}
	
	/**
	 * This file contains all the entries for the atomfeed. It is a separate file so that we can
	 * easily just append to it <br/>
	 * <br/>
	 * <b>NOTE:</b> It was intentionally made as protected to be available in tests
	 * 
	 * @return File of entries
	 * @see #getFeedHeaderFile()
	 */
	protected static File getFeedEntriesFile() {
		String folderName = Context.getAdministrationService().getGlobalProperty(AtomFeedConstants.GP_CACHE_DIRECTORY,
		    AtomFeedConstants.CACHE_DIRECTORY_DEFAULT);
		File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
		
		if (!dir.exists())
			dir.mkdirs();
		
		File f = new File(dir, "atomfeedentries");
			try {
				if (!f.exists())
					f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return f;
	}
	
	/**
	 * Updates content of atom feed header file by re-creating new xml header block and writing it
	 * into given file. Actually, if given atom feed header file does not exists, it creates it.
	 * Otherwise, it changes values of "updated", "versionId" and "entriesSize" elements within
	 * header xml tree.
	 * 
	 * @param atomfeedheader the file target to be updated
	 * @param entriesSize the size in bytes of entries payload, which is related to given feed
	 *            header
	 */
	private static void updateFeedFileHeader(File atomfeedheader, long entriesSize) {
		try {
			// We need a Document
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			// //////////////////////
			// Creating the XML tree
			
			// create the root element and add it to the document
			Element root = doc.createElement("feed");
			root.setAttribute("xmlns", "http://www.w3.org/2005/Atom");
			doc.appendChild(root);
			
			// create title element, add its text, and add to root
			Element title = doc.createElement("title");
			Text titleText = doc.createTextNode(AtomFeedConstants.ATOM_FEED_TITLE);
			title.appendChild(titleText);
			root.appendChild(title);
			
			// create title element, add its attrs, and add to root
			Element selflink = doc.createElement("link");
			selflink.setAttribute("href", AtomFeedUtil.getFeedUrl());
			selflink.setAttribute("rel", "self");
			root.appendChild(selflink);
			
			Element serverlink = doc.createElement("link");
			serverlink.setAttribute("href", getWebServiceUrl());
			root.appendChild(serverlink);
			
			// create title element, add its text, and add to root
			Element id = doc.createElement("id");
			Text idText = doc.createTextNode(AtomFeedConstants.ATOM_FEED_ID);
			id.appendChild(idText);
			root.appendChild(id);
			
			// create updated element, add its text, and add to root
			Element updated = doc.createElement("updated");
			Date lastModified = new Date();
			Text updatedText = doc.createTextNode(dateToRFC3339(lastModified));
			updated.appendChild(updatedText);
			root.appendChild(updated);
			
			// create versionId element, add its text, and add to root
			Element versionId = doc.createElement("versionId");
			Text versionIdText = doc.createTextNode(String.valueOf(lastModified.getTime()));
			versionId.appendChild(versionIdText);
			root.appendChild(versionId);
			
			// create versionId element, add its text, and add to root
			Element entriesSizeElement = doc.createElement("entriesSize");
			Text entriesSizeText = doc.createTextNode(String.valueOf(entriesSize));
			entriesSizeElement.appendChild(entriesSizeText);
			root.appendChild(entriesSizeElement);
			
			/*
			 * Print the xml to the file
			 */

			// set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			trans.setOutputProperty(OutputKeys.INDENT, "no");
			
			// create string from xml tree
			FileWriter fw = new FileWriter(atomfeedheader);
			StreamResult result = new StreamResult(fw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			
			// print xml for debugging purposes
			if (log.isTraceEnabled()) {
				StringWriter sw = new StringWriter();
				result = new StreamResult(sw);
				trans.transform(source, result);
				log.trace("Here's the initial xml:\n\n" + sw.toString());
			}
			
		}
		catch (Exception e) {
			log.error("unable to initialize feed at: " + atomfeedheader.getAbsolutePath(), e);
		}
	}
	
	/**
	 * @return self link to atom feed url. Uses the value of the 'webservices.rest.uriPrefix' global
	 *         property
	 * @see #REST_WEB_SERVICE_URL_GP_NAME
	 * @see AtomFeedConstants#ATOM_FEED_LINK_SUFFIX
	 * @should not fail if GP not defined
	 * @should only append one trailing slash
	 * @should append trailing slash if missing
	 */
	public static String getFeedUrl() {
		return getWebServiceUrl() + AtomFeedConstants.ATOM_FEED_LINK_SUFFIX;
	}
	
	/**
	 * A number formatting object to format the the timezone offset info in RFC3339 output.
	 */
	private static NumberFormat doubleDigit = new DecimalFormat("00");
	
	/**
	 * Format dates as specified in rfc3339 (required for Atom dates)
	 * 
	 * @param d the Date to be formatted
	 * @return the formatted date
	 * @should not fail given a null date
	 * @should convert date to rfc
	 */
	public static String dateToRFC3339(Date d) {
		if (d == null)
			return null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RFC_3339_DATE_FORMAT);
		Calendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.setTimeZone(TimeZone.getDefault());
		simpleDateFormat.setCalendar(cal);
		StringBuilder result = new StringBuilder(simpleDateFormat.format(d));
		int offset_millis = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
		int offset_hours = Math.abs(offset_millis / (1000 * 60 * 60));
		int offset_minutes = Math.abs((offset_millis / (1000 * 60)) % 60);
		
		if (offset_millis == 0) {
			result.append("Z");
		} else {
			result.append((offset_millis > 0) ? "+" : "-").append(doubleDigit.format(offset_hours)).append(":")
			        .append(doubleDigit.format(offset_minutes));
		}
		
		return result.toString();
	}
	
	/**
	 * Gets the date updated value for an openmrs object.
	 * 
	 * @param object the openmrs object
	 * @return the date updated
	 * @should return dateChanged value if present
	 * @should return dateCreated value if present and dateChanged is null or not present
	 */
	public static Date getUpdatedValue(OpenmrsObject object) {
		try {
			Object value = PropertyUtils.getProperty(object, "dateChanged");
			if (value != null) {
				return (Date) value;
			}
		}
		catch (Exception ex) {
			//ignore
		}
		
		try {
			Object value = PropertyUtils.getProperty(object, "dateCreated");
			if (value != null) {
				return (Date) value;
			}
		}
		catch (Exception ex) {
			//ignore
		}
		
		return new Date();
	}
	
	/**
	 * Gets the author for an openmrs object.
	 * 
	 * @param object the openmrs object
	 * @return the author
	 * @should return the author
	 * @should return creator if present and changedBy is null or not present
	 * @should return the system id of the user if username is blank
	 * @should return unknown if changedBy and creator are not set
	 */
	protected static String getAuthor(OpenmrsObject object) {
		try {
			Object value = PropertyUtils.getProperty(object, "changedBy");
			if (value != null) {
				return getAuthor((User) value);
			}
		}
		catch (Exception ex) {
			//ignore
		}
		
		try {
			Object value = PropertyUtils.getProperty(object, "creator");
			if (value != null) {
				return getAuthor((User) value);
			}
		}
		catch (Exception ex) {
			//ignore
		}
		
		return "Unknown";
	}
	
	protected static String getAuthor(User user) {
		StringBuilder author = new StringBuilder();
		
		PersonName personName = user.getPersonName();
		if (personName != null && !StringUtils.isBlank(personName.getFullName())) {
			author.append(personName.getFullName());
			author.append(" ");
		}
		
		author.append("(");
		author.append(StringUtils.isBlank(user.getUsername()) ? user.getSystemId() : user.getUsername());
		author.append(")");
		
		return author.toString();
	}
	
	/**
	 * Gets the value of the URL to the server where OpenMRS is running which is the same as that
	 * defined for the rest web services module
	 * 
	 * @return
	 */
	public static String getWebServiceUrl() {
		if (serverUrl == null) {
			serverUrl = Context.getAdministrationService().getGlobalProperty(restWebServiceUrlGPName);
			if (StringUtils.isBlank(serverUrl))
				serverUrl = "NEED-TO-CONFIGURE";
			if (!serverUrl.endsWith("/"))
				serverUrl += "/";
		}
		
		return serverUrl;
	}
	
	/**
	 * @see org.openmrs.api.GlobalPropertyListener#globalPropertyChanged(org.openmrs.GlobalProperty)
	 */
	@Override
	public void globalPropertyChanged(GlobalProperty newValue) {
		// reset the value
		serverUrl = null;
	}
	
	/**
	 * @see org.openmrs.api.GlobalPropertyListener#globalPropertyDeleted(java.lang.String)
	 */
	@Override
	public void globalPropertyDeleted(String propertyName) {
		serverUrl = null;
	}
	
	/**
	 * @see org.openmrs.api.GlobalPropertyListener#supportsPropertyName(java.lang.String)
	 */
	@Override
	public boolean supportsPropertyName(String propertyName) {
		return propertyName.equals(restWebServiceUrlGPName);
	}
}
