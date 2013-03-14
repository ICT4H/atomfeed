package org.ict4h.atomfeed.motechclient;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
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
import java.util.Arrays;
import java.util.HashMap;

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
        verifyZeroInteractions(eventRelay);
    }

    @Test
    public void shouldRelayEventContentToMotech() throws URISyntaxException {
        MotechAtomFeedConsumer consumer = new MotechAtomFeedConsumer(null,feedClient,"", eventRelay,null);
        ArrayList<Event> events = events();
        when(feedClient.unprocessedEvents(Matchers.<URI>any())).thenReturn(events);
        consumer.updateEvents(new MotechEvent("atomUpdateMessage"));
        HashMap map = new HashMap<String, Object>();
        map.put("contents",events.get(0).getContent());
        verify(eventRelay).sendEventMessage(new MotechEvent("eventFromOpenMRS",map));
    }

    @Test
    public void shouldMarkAnEventAsProcessedSoThatTheSameEventWillNotBePushedIntoTheQueueTheNextTimeWePollTheServer() throws URISyntaxException {
        URI uri = new URI("");
        ArrayList<Event> events = events();
        when(feedClient.unprocessedEvents(Matchers.<URI>any())).thenReturn(events);
        MotechAtomFeedConsumer consumer = new MotechAtomFeedConsumer(uri,feedClient,"", eventRelay,null);
        consumer.updateEvents(new MotechEvent());
        verify(feedClient).processedTo(uri, events.get(0).getId());
    }

    private ArrayList<Event> events() {
        ArrayList events = new ArrayList<Event>();
        Entry entry = new Entry();
        entry.setId("42");
        Content content = new Content();
        content.setValue("<data></data>");
        entry.setContents(Arrays.asList(content));
        Event event = new Event(entry);
        events.add(event);
        return events;
    }
}

