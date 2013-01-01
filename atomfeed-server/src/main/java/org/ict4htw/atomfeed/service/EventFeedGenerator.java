package org.ict4htw.atomfeed.service;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

//import com.sun.syndication.feed.atom.Category;
//import com.sun.syndication.feed.atom.Content;
//import com.sun.syndication.feed.atom.Entry;
//import com.sun.syndication.feed.atom.Feed;
//import com.sun.syndication.feed.atom.Generator;
//import com.sun.syndication.feed.atom.Link;
//import com.sun.syndication.feed.atom.Person;

public class EventFeedGenerator {
//    private static final String FEED_TITLE = "Restbucks products and promotions";
//    private static final String RESTBUCKS_MEDIA_TYPE = "application/vnd.restbucks+xml";
//    private static final String PRODUCING_SERVICE = "Product Catalog";
//    private static final String ATOM_MEDIA_TYPE = "application/atom+xml";
//    private URI requestUri;
//    private final int entriesPerFeed;
//
//    public EventFeedGenerator(URI requestUri, int entriesPerFeed) {
//        this.requestUri = requestUri;
//        this.entriesPerFeed = entriesPerFeed;
//    }
//
//    public Feed getWorkingFeed() {
//        int startEntry = findStartingEntryForHeadFeeds();
//        Feed working = feedFor(startEntry);
//
//        Link self = new Link();
//        self.setHref(this.generateCanonicalUri(startEntry));
//        self.setRel("self");
//        self.setType(ATOM_MEDIA_TYPE);
//        working.getAlternateLinks().add(self);
//
//        working.getAlternateLinks().addAll(generatePagingLinks(startEntry));
//
//        return working;
//    }
//
//    public Feed getRecentFeed() {
//        int startEntry = findStartingEntryForHeadFeeds();
//        Feed recent = feedFor(startEntry);
//
//        Link self = new Link();
//        self.setHref(requestUri.toString());
//        self.setRel("self");
//        self.setType(ATOM_MEDIA_TYPE);
//        recent.getAlternateLinks().add(self);
//
//        recent.getAlternateLinks().addAll(generatePagingLinks(startEntry));
//
//        Link via = new Link();
//        via.setHref(this.generateCanonicalUri(startEntry));
//        via.setRel("via");
//        via.setType(ATOM_MEDIA_TYPE);
//        recent.getAlternateLinks().add(via);
//
//
//        return recent;
//    }
//
//    private int findStartingEntryForHeadFeeds() {
//        int totalNumberOfEvents = EventStore.current().getNumberOfEvents();
//
//        int numberOfEventsInWorkingFeed = totalNumberOfEvents % entriesPerFeed;
//        if (numberOfEventsInWorkingFeed == 0) {
//            numberOfEventsInWorkingFeed = entriesPerFeed;
//        }
//
//        int startEntry = totalNumberOfEvents - numberOfEventsInWorkingFeed;
//        return startEntry;
//    }
//
//    public Feed getArchiveFeed(int startEntry) {
//        Feed archive = feedFor(startEntry);
//
//        Link self = new Link();
//        self.setHref(this.generateCanonicalUri(startEntry));
//        self.setRel("self");
//        self.setType(ATOM_MEDIA_TYPE);
//        archive.getAlternateLinks().add(self);
//
//        archive.getAlternateLinks().addAll(generatePagingLinks(startEntry));
//        return archive;
//    }
//
//    private Feed feedFor(int startEntry) {
//        Feed feed = new Feed();
//
//        feed.setFeedType("atom_1.0"); // Weak! This should be more strongly
//        // typed in Rome
//        feed.setId("urn:uuid:" + UUID.randomUUID().toString()); // We're
//        // naughty, in
//        // this example
//        // we don't need
//        // feed ID
//        // because we're
//        // not
//        // aggregating
//        // feeds
//        feed.setTitle(FEED_TITLE);
//        final Generator generator = new Generator();
//        generator.setUrl(getServiceUri());
//        generator.setValue(PRODUCING_SERVICE);
//        feed.setGenerator(generator);
//        feed.setAuthors(generateAuthorsList());
//        List<Event> events = EventStore.current().getEvents(startEntry,
//                entriesPerFeed);
//        feed.setEntries(createEntries(events));
//        feed.setUpdated(newestEventDate(events));
//
//        return feed;
//    }
//
//    private Date newestEventDate(List<Event> events) {
//        Date date = oldDate();
//        for (Event e : events) {
//            if (e.getTimestamp().after(date)) {
//                date = e.getTimestamp();
//            }
//        }
//
//        return date;
//    }
//
//    private Date oldDate() {
//        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            return dfm.parse("1900-01-1");
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private String getServiceUri() {
//        String scheme = requestUri.getScheme();
//        String hostname = requestUri.getHost();
//        int port = requestUri.getPort();
//        String path = requestUri.getPath().substring(0,
//                requestUri.getPath().lastIndexOf("/"));
//        if (port != 80 && port != -1) {
//            return scheme + "://" + hostname + ":" + port + path;
//        } else {
//            return scheme + "://" + hostname + path;
//        }
//
//    }
//
//    private List<Person> generateAuthorsList() {
//        ArrayList<Person> result = new ArrayList<Person>();
//        final Person person = new Person();
//        person.setName("A Product Manager");
//        result.add(person);
//        return result;
//    }
//
//    private List<Link> generatePagingLinks(int currentFeedStart) {
//        ArrayList<Link> links = new ArrayList<Link>();
//
//        if (hasNewerFeed(currentFeedStart)) {
//            Link next = new Link();
//            next.setRel("next-archive");
//            next.setType(ATOM_MEDIA_TYPE);
//            next.setHref(generatePageUri(getServiceUri(), currentFeedStart + entriesPerFeed));
//            links.add(next);
//        }
//
//        if (hasOlderFeed(currentFeedStart)) {
//            Link prev = new Link();
//            prev.setRel("prev-archive");
//            prev.setType(ATOM_MEDIA_TYPE);
//            prev.setHref(generatePageUri(getServiceUri(), currentFeedStart
//                    - entriesPerFeed));
//            links.add(prev);
//        }
//
//        return links;
//    }
//
//    private String generatePageUri(String serviceUri, int startFrom) {
//        return getServiceUri() + "/" + startFrom + ","
//                + (startFrom + entriesPerFeed - 1);
//    }
//
//    private String generateCanonicalUri(int startFrom) {
//        return getServiceUri() + "/" + startFrom + "," + (startFrom + entriesPerFeed - 1);
//    }
//
//    private boolean hasOlderFeed(int currentPosition) {
//        return currentPosition - entriesPerFeed >= 0;
//    }
//
//    private boolean hasNewerFeed(int startFrom) {
//        return startFrom + entriesPerFeed < EventStore.current()
//                .getNumberOfEvents();
//    }
//
//    private List<Entry> createEntries(List<Event> events) {
//        ArrayList<Entry> entries = new ArrayList<Entry>();
//
//        for (Event e : events) {
//            final Entry entry = new Entry();
//            entry.setId(e.getTagUri());
//            entry.setTitle(e.getEventType());
//            entry.setUpdated(e.getTimestamp());
//            entry.setAlternateLinks(generateLinks(e));
//            entry.setCategories(generateCategories(e));
//            entry.setContents(generateContents(e));
//            entries.add(entry);
//        }
//
//        return entries;
//    }
//
//    private List<Content> generateContents(Event event) {
//        ArrayList<Content> contents = new ArrayList<Content>();
//
//        Content content = new Content();
//        content.setType(RESTBUCKS_MEDIA_TYPE);
//        content.setValue(event.toXmlString());
//        contents.add(content);
//
//        return contents;
//    }
//
//    private List<Category> generateCategories(Event e) {
//        ArrayList<Category> categories = new ArrayList<Category>();
//
//        Category eventType = new Category();
//        eventType.setTerm(e.getEventType());
//        eventType.setScheme(getServiceUri() + "/categories/type");
//        categories.add(eventType);
//
//        Category status = new Category();
//        status.setTerm(e.getEventStatus());
//        status.setScheme(getServiceUri() + "/categories/status");
//        categories.add(status);
//
//        return categories;
//    }
//
//    private List<Link> generateLinks(Event event) {
//        ArrayList<Link> links = new ArrayList<Link>();
//
//        Link self = new Link();
//        self.setHref(getServiceUri() + "/notifications/" + event.getId());
//        self.setRel("self");
//
//        Link related = new Link();
//        related.setHref(event.getAssociatedUri());
//        related.setRel("related");
//
//        links.add(self);
//        links.add(related);
//
//        return links;
//    }
}