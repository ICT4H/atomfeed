package org.ict4h.atomfeed.client.repository.datasource;

import org.ict4h.atomfeed.client.AtomFeedProperties;

import java.net.URI;
import java.util.Map;

public interface HttpClient {
    String fetch(URI uri, AtomFeedProperties atomFeedProperties, Map<String, String> clientCookies);
}
