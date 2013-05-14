package org.ict4h.atomfeed.client.factory;

import org.ict4h.atomfeed.client.repository.AllFailedEvents;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.AllMarkers;
import org.ict4h.atomfeed.client.repository.datasource.WebClient;
import org.ict4h.atomfeed.client.service.AtomFeedClient;

public class AtomClientFactory {
    public AtomFeedClient create(AllMarkers allMarkers, AllFailedEvents allFailedEvents){
        return new AtomFeedClient(new AllFeeds(new WebClient()), allMarkers, allFailedEvents);
    }
}
