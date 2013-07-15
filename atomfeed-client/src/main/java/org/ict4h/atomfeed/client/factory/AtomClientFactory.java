package org.ict4h.atomfeed.client.factory;

import org.ict4h.atomfeed.Configuration;
import org.ict4h.atomfeed.client.repository.AllFailedEvents;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.AllMarkers;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;

public class AtomClientFactory {
    public AtomFeedClient create(AllMarkers allMarkers, AllFailedEvents allFailedEvents) {
        return new AtomFeedClient(new AllFeeds(), allMarkers, allFailedEvents, Configuration.getInstance().getUpdateAtomFeedMarkerFlag());
    }

    public AtomFeedClient create(JdbcConnectionProvider connectionProvider) {
        return create(new AllMarkersJdbcImpl(connectionProvider), new AllFailedEventsJdbcImpl(connectionProvider));
    }
}
