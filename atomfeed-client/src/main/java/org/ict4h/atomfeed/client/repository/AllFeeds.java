package org.ict4h.atomfeed.client.repository;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.WireFeedInput;
import org.apache.log4j.Logger;
import org.ict4h.atomfeed.client.exceptions.AtomFeedClientException;
import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.ict4h.atomfeed.client.repository.datasource.DefaultHttpClient;
import org.ict4h.atomfeed.client.repository.datasource.HttpClient;

import java.io.StringReader;
import java.net.URI;
import java.util.Map;

public class AllFeeds {
    private HttpClient httpClient;
    private AtomFeedProperties atomFeedProperties = new AtomFeedProperties();
    private Map<String, String> clientCookies;

    private static Logger logger = Logger.getLogger(AllFeeds.class);

    protected AllFeeds() {
        this.httpClient = new DefaultHttpClient();
    }

    public AllFeeds(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public AllFeeds(AtomFeedProperties atomFeedProperties, Map<String, String> clientCookies) {
        this();
        this.atomFeedProperties = atomFeedProperties;
        this.clientCookies = clientCookies;
    }

    public AllFeeds(HttpClient httpClient, AtomFeedProperties atomFeedProperties, Map<String, String> clientCookies) {
        this.httpClient = httpClient;
        this.atomFeedProperties = atomFeedProperties;
        this.clientCookies = clientCookies;
    }

    public Feed getFor(URI uri) {
    	if (uri == null) return null;

        logger.info(String.format("Reading URI - %s", uri));
        String responseString = httpClient.fetch(uri, atomFeedProperties, clientCookies);
        logger.debug(responseString);
        responseString.trim().replaceFirst("^([\\W]+)<", "<");

        try {
            WireFeedInput input = new WireFeedInput();
            Feed feed = (Feed) input.build(new StringReader(responseString));
            logger.info(String.format("Found %d entries", feed.getEntries().size()));
            return feed;
        } catch (Exception e) {
            throw new AtomFeedClientException(responseString, e);
        }
    }
}