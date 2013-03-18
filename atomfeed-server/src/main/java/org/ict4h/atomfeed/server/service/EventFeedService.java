package org.ict4h.atomfeed.server.service;

import java.net.URI;

import com.sun.syndication.feed.atom.Feed;

public interface EventFeedService {

	public abstract Feed getRecentFeed(URI requestUri);

	public abstract Feed getEventFeed(URI requestUri, Integer feedId);

}