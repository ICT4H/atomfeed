package org.ict4h.atomfeed.client;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import org.apache.log4j.Logger;
import org.ict4h.atomfeed.client.domain.Marker;
import org.ict4h.atomfeed.client.repository.AllFeeds;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FeedEnumerator implements Iterable<Entry>, Iterator<Entry> {
    private AllFeeds allFeeds;
    private Marker marker;

    private Feed currentFeed;
    private List<Entry> entries;

    private static Logger logger = Logger.getLogger(FeedEnumerator.class);

    public FeedEnumerator(AllFeeds allFeeds, Marker marker) {
        this.allFeeds = allFeeds;
        this.marker = marker;

        initializeEnumeration();
    }

    private void initializeEnumeration() {
        // No entry from feed has been processed yet.
        if (marker.getLastReadEntryId() == null) {
            Feed feed = prefetchAllFeeds(marker.getFeedUri());
            this.currentFeed = feed;
            this.entries = feed.getEntries();
            return;
        }

        setInitialEntries(marker.getFeedURIForLastReadEntry());
    }

    private Feed prefetchAllFeeds(URI uri) {
        Feed currentFeed;
        do {
            currentFeed = allFeeds.getFor(uri);
        } while ((uri = prevArchive(currentFeed)) != null);
        return currentFeed;
    }

    private void setInitialEntries(URI feedURI) {
        Feed feed = allFeeds.getFor(feedURI);
        List<Entry> initialEntries = feed.getEntries();

        int lastReadEntryIndex = -1;
        for (int i = 0; i < initialEntries.size(); i++) {
            if (initialEntries.get(i).getId().equals(marker.getLastReadEntryId())) {
                lastReadEntryIndex = i;
                break;
            }
        }
        if (lastReadEntryIndex == -1) throw new RuntimeException("Last Read entry not found in feed.");

        initialEntries.removeAll(initialEntries.subList(0, lastReadEntryIndex + 1));

        this.entries = initialEntries;
        this.currentFeed = feed;
    }

    private URI getArchive(Feed feed, String archiveType) {
        try {
            for (Object obj : feed.getOtherLinks()) {
                Link l = (Link) obj;
                if (l.getRel().equals(archiveType)) {
                    return new URI(l.getHref());
                }
            }
            return null;
        } catch (URISyntaxException e) {
            throw new RuntimeException("Bad archive link.");
        }
    }

    private URI nextArchive(Feed feed) {
        return getArchive(feed, "next-archive");
    }

    private URI prevArchive(Feed feed) {
        return getArchive(feed, "prev-archive");
    }

    private void fetchEntries() {
        URI nextArchiveUri = nextArchive(currentFeed);
        if (nextArchiveUri != null && entries.isEmpty()) {
            this.currentFeed = allFeeds.getFor(nextArchiveUri);
            this.entries = currentFeed.getEntries();
        }
    }

    @Override
    public boolean hasNext() {
        if (!entries.isEmpty()) return true;

        if (nextArchive(this.currentFeed) == null) return false;

        fetchEntries(); return hasNext();
    }

    @Override
    public Entry next() {
        Entry entry = entries.get(0);
        remove();
        return entry;
    }

    @Override
    public void remove() {
        entries.remove(0);
    }

    @Override
    public Iterator<Entry> iterator() {
        return this;
    }
}