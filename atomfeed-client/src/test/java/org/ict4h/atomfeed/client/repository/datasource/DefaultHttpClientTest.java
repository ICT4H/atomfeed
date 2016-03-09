package org.ict4h.atomfeed.client.repository.datasource;

import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.util.HashMap;

import static org.junit.Assert.*;

public class DefaultHttpClientTest {

    @Test
    @Ignore
    public void testFetch() throws Exception {
        final AtomFeedProperties feedProperties = new AtomFeedProperties();
        String url = "http://bdshr-tr.twhosted.com/openmrs/ws/atomfeed/concept/recent";
        final DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        final String response = defaultHttpClient.fetch(new URI(url), feedProperties, new HashMap<String, String>());
        System.out.println(response);

    }
}