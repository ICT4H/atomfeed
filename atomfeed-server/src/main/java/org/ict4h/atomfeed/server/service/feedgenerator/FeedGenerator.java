package org.ict4h.atomfeed.server.service.feedgenerator;

import org.ict4h.atomfeed.server.domain.EventFeed;

public interface FeedGenerator {
    EventFeed getFeedForId(Integer feedId, String category);
    EventFeed getRecentFeed();
}
