package org.ict4htw.atomfeed.client;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import org.apache.log4j.Logger;
import org.ict4htw.atomfeed.client.repository.AllFeeds;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FeedEnumerator {
    private AllFeeds allFeeds;
    private URI startingURI;
    private static Logger logger = Logger.getLogger(FeedEnumerator.class);

    public FeedEnumerator(AllFeeds allFeeds, URI startingURI) {
        this.allFeeds = allFeeds;
        this.startingURI = startingURI;
    }

    public List<Entry> newerEntries(String lastReadEntryId) throws URISyntaxException {
        List<Entry> entries = new ArrayList<Entry>();
        Iterable<Entry> history = history();
        for (Entry entry : history) {
            if (entry.getId().equals(lastReadEntryId)) {
                Collections.reverse(entries);
                return entries;
            }
            entries.add(entry);
        }
        throw new RuntimeException("Entry not found.");
    }

    public List<Entry> getAllEntries() throws URISyntaxException {
        List<Entry> entries = new ArrayList<Entry>();
        for (Entry entry : history()) {
            entries.add(entry);
        }
        Collections.reverse(entries);
        return entries;
    }

    private URI getUriFromNamedLink(String relValue, Feed feed) throws URISyntaxException {
        for (Object obj : feed.getOtherLinks()) {
            Link l = (Link) obj;
            if (l.getRel().equals(relValue)) {
                return new URI(l.getHref());
            }
        }
        return null;
    }

    private Iterable<Entry> history() {
        return new Iterable<Entry>() {
            @Override
            public Iterator<Entry> iterator() {
                return new Iterator<Entry>() {

                    Feed feed = allFeeds.getFor(startingURI);
                    List<Entry> entries;

                    {
                        setEntries();
                    }

                    private void setEntries() {
                        entries = feed.getEntries();
                        Collections.reverse(entries);
                    }

                    private URI prev() {
                        try {
                            return getUriFromNamedLink("prev-archive", feed);
                        } catch (URISyntaxException e) {
                            throw new RuntimeException("Bad prev link.");
                        }
                    }

                    @Override
                    public boolean hasNext() {
                        return !entries.isEmpty();
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
                        if (prev() != null && entries.isEmpty()) {
                            feed = allFeeds.getFor(prev());
                            setEntries();
                        }
                    };
                };
            }
        };
    }
}