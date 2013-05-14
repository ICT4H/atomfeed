package org.ict4h.atomfeed.client;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import org.apache.log4j.Logger;
import org.ict4h.atomfeed.client.domain.Marker;
import org.ict4h.atomfeed.client.exceptions.AtomFeedClientException;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.util.Util;

import java.net.URI;
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
            Feed feed = seekFirstFeed(marker.getFeedUri());
            this.currentFeed = feed;
            this.entries = feed.getEntries();
            return;
        }

        setInitialEntries(marker.getFeedURIForLastReadEntry());
    }

    private Feed seekFirstFeed(URI uri) {
        Feed currentFeed;
        do {
            currentFeed = allFeeds.getFor(uri);
        } while ((uri = Util.getPreviousLink(currentFeed)) != null);
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
        if (lastReadEntryIndex == -1) throw new AtomFeedClientException("Last Read entry not found in feed.");

        initialEntries.removeAll(initialEntries.subList(0, lastReadEntryIndex + 1));

        this.entries = initialEntries;
        this.currentFeed = feed;
    }

    private void fetchEntries() {
        URI nextArchiveUri = Util.getNextLink(currentFeed);
        if (nextArchiveUri != null && entries.isEmpty()) {
            this.currentFeed = allFeeds.getFor(nextArchiveUri);
            this.entries = currentFeed.getEntries();
        }
    }

    @Override
    public boolean hasNext() {
        if (!entries.isEmpty()) return true;
        if (Util.getNextLink(this.currentFeed) == null) return false;
        fetchEntries();
        return hasNext();
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

    public Feed getCurrentFeed() {
        return currentFeed;
    }
}