package org.ict4htw.atomfeed.server.service.feedgenerator;

import org.ict4htw.atomfeed.server.domain.EventFeed;

public interface FeedGenerator {
    EventFeed getFeedForId(Integer feedId);

    EventFeed getRecentFeed();

    int getFeedCount(int limit);
}
