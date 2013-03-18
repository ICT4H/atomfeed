package org.ict4h.atomfeed.motechclient;

import org.ict4h.atomfeed.client.api.AtomFeedClient;
import org.ict4h.atomfeed.client.api.FeedClient;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.AllMarkers;
import org.ict4h.atomfeed.client.repository.datasource.WebClient;

public class AtomClientFactory {
    public FeedClient get(){
        return new AtomFeedClient(new AllFeeds(new WebClient()),new AllMarkers(new InmemoryMarkerDataSource()));
    }
}
