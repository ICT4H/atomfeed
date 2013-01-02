package org.ict4htw.atomfeed.service;

import com.sun.syndication.feed.atom.*;
import org.ict4htw.atomfeed.domain.EventRecord;
import org.ict4htw.atomfeed.feed.FeedBuilder;
import org.ict4htw.atomfeed.repository.AllEventRecords;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class EventFeedService {

    private AllEventRecords allEventRecords;

    private URI requestUri;

    private final int ENTRIES_PER_FEED = 5;

    private static final String ATOM_MEDIA_TYPE = "application/atom+xml";
    private static final String LINK_TYPE_SELF = "self";
    private static final String LINK_TYPE_VIA = "via";
    private static final String ATOMFEED_MEDIA_TYPE = "application/vnd.atomfeed+xml";

    public EventFeedService(String requestURI, AllEventRecords allEventRecords) {
        try {
            this.requestUri = new URI(requestURI);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Bad URI", e);
        }
        this.allEventRecords = allEventRecords;
    }

    public Feed getRecentFeed() {
        int totalNumberOfEvents = allEventRecords.getTotalCount();

        int numberOfEventsInFeed = totalNumberOfEvents % ENTRIES_PER_FEED;
        if (numberOfEventsInFeed == 0) numberOfEventsInFeed = ENTRIES_PER_FEED;

        int startEventNumber = totalNumberOfEvents - numberOfEventsInFeed;

        List<EventRecord> eventRecordList = allEventRecords.getEventsFromNumber(startEventNumber, ENTRIES_PER_FEED);

        return new FeedBuilder()
                .type("atom_1.0")
                        // Presence of feed ID not necessary. We might choose to remove it
                .id("urn:uuid:" + UUID.randomUUID().toString())
                .title("TITLE") // TODO: This should be dictated by the application using it.
                .generator(getGenerator())
//                .authors() // TODO : Use Person class or rome and link to OpenMRS URI for user
                .entries(getEntries(eventRecordList))
                .updated(newestEventDate(eventRecordList))
                .link(getLink(requestUri.toString(), LINK_TYPE_SELF, ATOM_MEDIA_TYPE))
                .link(getLink(generateCanonicalUri(startEventNumber), LINK_TYPE_VIA, ATOM_MEDIA_TYPE))
                .links(generatePagingLinks(startEventNumber, totalNumberOfEvents))
                .build();

    }

    public Feed getEventFeed(int startPos, int endPos) {
        int totalNumberOfEvents = allEventRecords.getTotalCount();

        if (invalidStartAndEndPos(startPos, endPos, totalNumberOfEvents))
            throw new IllegalArgumentException("Invalid arguments for startPos or endPos");

        List<EventRecord> eventRecordList = allEventRecords.getEventsFromNumber(startPos, ENTRIES_PER_FEED);

        return new FeedBuilder()
                .type("atom_1.0")
                .id("urn:uuid:" + UUID.randomUUID().toString())
                .title("TITLE")
                .generator(getGenerator())
                .entries(getEntries(eventRecordList))
                .updated(newestEventDate(eventRecordList))
                .link(getLink(requestUri.toString(), LINK_TYPE_SELF, ATOM_MEDIA_TYPE))
                .links(generatePagingLinks(startPos, totalNumberOfEvents))
                .build();

    }

    private Generator getGenerator() {
        Generator generator = new Generator();
        generator.setUrl("http://uri");
        generator.setValue("FEED_SERVICE");
        return generator;
    }

    private Date newestEventDate(List<EventRecord> eventRecordList) {
        return Collections.max(eventRecordList, new Comparator<EventRecord>() {
            @Override
            public int compare(EventRecord o1, EventRecord o2) {
                return o1.getTimeStamp().compareTo(o2.getTimeStamp());
            }
        }).getTimeStamp();
    }

    private Link getLink(String href, String rel, String type) {
        Link link = new Link();

        link.setHref(href);
        link.setRel(rel);
        link.setType(type);

        return link;
    }

    private String getServiceUri() {
        String scheme = requestUri.getScheme();
        String hostname = requestUri.getHost();
        int port = requestUri.getPort();
        String path = requestUri.getPath().substring(0,
                requestUri.getPath().lastIndexOf("/"));
        if (port != 80 && port != -1) {
            return scheme + "://" + hostname + ":" + port + path;
        } else {
            return scheme + "://" + hostname + path;
        }
    }

    private String generateCanonicalUri(int startFrom) {
        return getServiceUri() + "/" + startFrom + "," + (startFrom + ENTRIES_PER_FEED - 1);
    }

    private boolean hasOlderFeed(int currentPosition) {
        return currentPosition - ENTRIES_PER_FEED >= 0;
    }

    private boolean hasNewerFeed(int startFrom, int totalNumberOfEvents) {
        return startFrom + ENTRIES_PER_FEED < totalNumberOfEvents;
    }

    private List<Link> generatePagingLinks(int currentFeedStart, int totalNumberOfEvents) {
        ArrayList<Link> links = new ArrayList<>();

        if (hasNewerFeed(currentFeedStart, totalNumberOfEvents)) {
            Link next = new Link();
            next.setRel("next-archive");
            next.setType(ATOM_MEDIA_TYPE);
            next.setHref(generateCanonicalUri(currentFeedStart + ENTRIES_PER_FEED));
            links.add(next);
        }

        if (hasOlderFeed(currentFeedStart)) {
            Link prev = new Link();
            prev.setRel("prev-archive");
            prev.setType(ATOM_MEDIA_TYPE);
            prev.setHref(generateCanonicalUri(currentFeedStart - ENTRIES_PER_FEED));
            links.add(prev);
        }

        return links;
    }

    private boolean invalidStartAndEndPos(int startPos, int endPos, int totalNumberOfEvents) {
        return !(startPos % ENTRIES_PER_FEED != 0 || endPos != startPos + ENTRIES_PER_FEED - 1) && !(startPos < 0 || startPos > totalNumberOfEvents);

    }

    private List<Entry> getEntries(List<EventRecord> eventRecordList) {
        List<Entry> entryList = new ArrayList<>();

        for (EventRecord eventRecord : eventRecordList) {
            final Entry entry = new Entry();
            entry.setId(eventRecord.getTagUri());
            entry.setTitle(eventRecord.getTitle());
            entry.setUpdated(eventRecord.getTimeStamp());
            entry.setAlternateLinks(generateLinks(eventRecord));
            entry.setContents(generateContents(eventRecord));
            entryList.add(entry);
        }

        return entryList;
    }

    private List<Link> generateLinks(EventRecord eventRecord) {
        ArrayList<Link> links = new ArrayList<>();

        Link self = new Link();
        self.setHref(getServiceUri() + "/events/" + eventRecord.getId());
        self.setRel("self");

        Link related = new Link();
        related.setHref(eventRecord.getUri());
        related.setRel("related");

        links.add(self);
        links.add(related);

        return links;
    }

    private List<Content> generateContents(EventRecord eventRecord) {
        ArrayList<Content> contents = new ArrayList<>();

        Content content = new Content();
        content.setType(ATOMFEED_MEDIA_TYPE);
        content.setValue(eventRecord.toXmlString());
        contents.add(content);

        return contents;
    }
}
