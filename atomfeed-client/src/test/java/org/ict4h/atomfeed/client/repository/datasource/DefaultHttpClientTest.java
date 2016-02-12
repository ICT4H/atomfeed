package org.ict4h.atomfeed.client.repository.datasource;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.commons.lang3.StringUtils;
import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.ict4h.atomfeed.client.exceptions.AtomFeedClientException;
import org.junit.Rule;
import org.junit.Test;

import java.net.URI;
import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;

public class DefaultHttpClientTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Test
    public void shouldFollowRedirectionIfEnabled() throws Exception {
        final AtomFeedProperties feedProperties = new AtomFeedProperties();
        feedProperties.setHandleRedirection(true);
        String response = "all fine";
        String givenUrl = "/openmrs/ws/atomfeed/concept/recent";
        String url = "http://localhost:8089" + givenUrl;
        String redirectUrl = "/openmrs/ws/atomfeed/concepts/4";
        stubFor(get(urlEqualTo(givenUrl))
                .willReturn(aResponse()
                        .withStatus(302)
                        .withHeader("Location", "http://localhost:8089" + redirectUrl)
                        .withBody("")));
        stubFor(get(urlEqualTo(redirectUrl))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(response)));
        final DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        final String actualResponse = defaultHttpClient.fetch(new URI(url), feedProperties, new HashMap<String, String>());
        assertEquals(response, StringUtils.trim(actualResponse));
    }

    @Test(expected = AtomFeedClientException.class)
    public void shouldNotFollowRedirectionIfDisabled() throws Exception {
        final AtomFeedProperties feedProperties = new AtomFeedProperties();
        feedProperties.setHandleRedirection(false);
        String url = "http://localhost:8089/openmrs/ws/atomfeed/concept/recent";
        stubFor(get(urlEqualTo("/openmrs/ws/atomfeed/concept/recent"))
                .willReturn(aResponse()
                        .withStatus(302)
                        .withHeader("Location", "http://localhost:8089/openmrs/ws/atomfeed/concepts/4")
                        .withBody("")));
        final DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        defaultHttpClient.fetch(new URI(url), feedProperties, new HashMap<String, String>());
    }
}