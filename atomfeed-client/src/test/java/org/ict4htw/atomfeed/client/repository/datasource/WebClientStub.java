package org.ict4htw.atomfeed.client.repository.datasource;

import org.ict4htw.atomfeed.server.resource.EventResource;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

import static org.mockito.Mockito.when;

public class WebClientStub extends WebClient {
    private EventResource eventResource;
    private HttpServletRequest httpServletRequest;

    public WebClientStub(EventResource eventResource) {
        this.eventResource = eventResource;
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
    }

    @Override
    public String fetch(URI uri) {
        when(httpServletRequest.getRequestURI()).thenReturn(uri.toString());
        return eventResource.getRecentEventFeed(httpServletRequest);
    }
}