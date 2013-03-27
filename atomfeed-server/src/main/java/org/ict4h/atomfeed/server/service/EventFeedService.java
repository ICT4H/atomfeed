package org.ict4h.atomfeed.server.service;

import java.net.URI;

import com.sun.syndication.feed.atom.Feed;

/**
 * The interface {@code EventFeedService} contains methods to retrieve a {@code Feed}.
 *
 */
public interface EventFeedService {

    /**
     * Returns the most recent {@code Feed}
     * @param requestUri  a URI that identifies the recent {@code Feed}.
     * @return The {@code Feed} identified by the requestUri
     */
	public abstract Feed getRecentFeed(URI requestUri);

    /**
     * Returns the {@code Feed} given a {@code URI} and {@code Integer} identifier for a feed
     * @param requestUri a URI that is the request for the given feed
     * @param feedId an integer that refers the the feed
     * @return The {@code Feed} identified by the feedId parameter
     */
	public abstract Feed getEventFeed(URI requestUri, Integer feedId);

}