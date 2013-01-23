package org.ict4htw.atomfeed.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ict4htw.atomfeed.client.domain.Entries;
import org.ict4htw.atomfeed.client.repository.AllFeeds;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;

public class FeedEnumerator {
    private AllFeeds allFeeds;
    private URI startingURI;
    private static Logger logger = Logger.getLogger(FeedEnumerator.class);

    public FeedEnumerator(AllFeeds allFeeds, URI startingURI) {
        this.allFeeds = allFeeds;
        this.startingURI = startingURI;
    }

    private Feed navigateToFeedEntry(String feedEntryId) throws URISyntaxException {
        return feedWith(startingURI, feedEntryId);
    }

    private Feed feedWith(URI uri, String feedEntryId) throws URISyntaxException {
        Feed feed = allFeeds.getFor(uri);
        Entries feedEntries = new Entries(feed.getEntries());
        if (feedEntries.getEntryWith(feedEntryId) != null)
            return feed;
        URI prevArchiveURI = getPrevArchiveURI(feed);
		if (prevArchiveURI != null)
			return feedWith(prevArchiveURI, feedEntryId);
		else
			throw new RuntimeException("Entry does not exist in any feed.");
    }    

	public List<Entry> newerEntries(String lastReadEntryId) throws URISyntaxException {
        List<Entry> entryList = new ArrayList<Entry>();
        Feed feed = navigateToFeedEntry(lastReadEntryId);
        if (feed == null) return entryList;

        // Add all the entries in that feedgenerator which are newer
        Entries entries = new Entries(feed.getEntries());
        entryList.addAll(entries.newerEntries(lastReadEntryId));

        // Recurse through all the newer feeds and add all entries
        addNewerEntries(feed, entryList);
        return entryList;
    }
	
	public List<Entry> getAllEntries() throws URISyntaxException {
		URI feedURI = startingURI;
		List<Entry> entries = new ArrayList<Entry>();
		do {
			Feed feed = allFeeds.getFor(feedURI);
			List<Entry> updatedList = feed.getEntries();
			updatedList.addAll(entries);
			entries = updatedList;
			feedURI = getPrevArchiveURI(feed);
		} while (feedURI != null);
		return entries;
	}
	
	
	

    private void addNewerEntries(Feed feed, List<Entry> entryList) throws URISyntaxException {
        URI uri = getNextArchive(feed);
        Feed currentFeed = allFeeds.getFor(uri);
        if (currentFeed != null) {
            entryList.addAll(currentFeed.getEntries());
            addNewerEntries(currentFeed, entryList);
        }
    }

    private URI getPrevArchiveURI(Feed feed) throws URISyntaxException {
        URI uriFromNamedLink = getUriFromNamedLink("prev-archive", feed);
        logger.info("prev-link URI: " + uriFromNamedLink);
        return uriFromNamedLink;
    }

    private URI getNextArchive(Feed feed) throws URISyntaxException {
        return getUriFromNamedLink("next-archive", feed);
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
}