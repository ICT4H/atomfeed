package org.ict4h.atomfeed.client.factory;

import org.ict4h.atomfeed.client.api.AtomFeedClient;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.AllMarkers;
import org.ict4h.atomfeed.client.repository.datasource.MarkerDataSource;
import org.ict4h.atomfeed.client.repository.datasource.WebClient;

public class AtomClientFactory {
    public AtomFeedClient create(MarkerDataSource markerDataSource){
        return new AtomFeedClient(new AllFeeds(new WebClient()), new AllMarkers(markerDataSource));
    }
}
