package org.ict4htw.atomfeed.motechclient;

import org.ict4htw.atomfeed.client.api.FeedClient;
import org.ict4htw.atomfeed.client.api.data.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class MotechAtomFeedConsumerTest {
    private EventRelay eventRelay;
    private FeedClient feedClient;

    @Before
    public void setUp(){
        feedClient = mock(FeedClient.class);
        eventRelay = mock(EventRelay.class);
    }

    @Test
    public void shouldNotAddEventsToTheQueueWhenNoUnprocessedEventsAreFound() throws URISyntaxException {
        MotechAtomFeedConsumer consumer = new MotechAtomFeedConsumer(null,feedClient,"", eventRelay,null);
        when(feedClient.unprocessedEvents(Matchers.<URI>any())).thenReturn(new ArrayList<Event>());
        consumer.updateEvents(new MotechEvent());
        verify(eventRelay,never()).sendEventMessage(null);
    }
}

