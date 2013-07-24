package org.ict4h.atomfeed.client.repository;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.WireFeedInput;
import org.apache.log4j.Logger;
import org.ict4h.atomfeed.client.exceptions.AtomFeedClientException;
import org.ict4h.atomfeed.client.factory.AtomFeedProperties;
import org.ict4h.atomfeed.client.repository.datasource.WebClient;

import java.io.StringReader;
import java.net.URI;

public class AllFeeds {
    private WebClient webClient;
    private AtomFeedProperties atomFeedProperties = new AtomFeedProperties();

    private static Logger logger = Logger.getLogger(AllFeeds.class);

    public AllFeeds() {
        this.webClient = new WebClient();
    }

    public AllFeeds(AtomFeedProperties atomFeedProperties) {
        this();
        this.atomFeedProperties = atomFeedProperties;
    }

    public Feed getFor(URI uri) {
    	if (uri == null) return null;
        String responseString = webClient.fetch(uri, atomFeedProperties);
        logger.debug(responseString);
        responseString.trim().replaceFirst("^([\\W]+)<", "<");

        try {
            WireFeedInput input = new WireFeedInput();
            return (Feed) input.build(new StringReader(responseString));
        } catch (Exception e) {
            throw new AtomFeedClientException(responseString, e);
        }
    }
}