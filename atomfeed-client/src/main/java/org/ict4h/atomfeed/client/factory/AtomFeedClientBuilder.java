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

    // TODO :Mujir - add properties for webclient timeout etc..
    // public AtomFeedClientBuilder with(AtomFeedProperties properties) {}

    public AtomFeedClient build () {
        return new AtomFeedClient(new AllFeeds(), new AllMarkersJdbcImpl(jdbcConnectionProvider), new AllFailedEventsJdbcImpl(jdbcConnectionProvider),
                Configuration.getInstance().getUpdateAtomFeedMarkerFlag(), jdbcConnectionProvider, feedURI, eventWorker);
    }

}
