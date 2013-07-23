package org.ict4h.atomfeed.client.factory;

import org.ict4h.atomfeed.Configuration;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;

import java.net.URI;

public class AtomFeedClientBuilder {
    private URI feedURI;
    private EventWorker eventWorker;
    private JdbcConnectionProvider jdbcConnectionProvider;
    private AtomFeedProperties atomFeedProperties = new AtomFeedProperties();

    public AtomFeedClientBuilder forFeedAt(URI feedURI) {
        this.feedURI = feedURI;
        return this;
    }

    public AtomFeedClientBuilder processedBy(EventWorker eventWorker) {
        this.eventWorker = eventWorker;
        return this;
    }

    public AtomFeedClientBuilder usingConnectionProvider(JdbcConnectionProvider jdbcConnectionProvider) {
        this.jdbcConnectionProvider = jdbcConnectionProvider;
        return this;
    }

    public AtomFeedClientBuilder with(AtomFeedProperties atomFeedProperties) {
        this.atomFeedProperties = atomFeedProperties;
        return this;
    }

    public AtomFeedClient build () {
        AllFeeds allFeeds = new AllFeeds(atomFeedProperties);

        return new AtomFeedClient(allFeeds, new AllMarkersJdbcImpl(jdbcConnectionProvider), new AllFailedEventsJdbcImpl(jdbcConnectionProvider),
                Configuration.getInstance().getUpdateAtomFeedMarkerFlag(), jdbcConnectionProvider, feedURI, eventWorker);
    }

}


