package org.ict4h.atomfeed.client.service;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.domain.FailedEvent;
import org.ict4h.atomfeed.client.exceptions.AtomFeedClientException;
import org.ict4h.atomfeed.client.repository.AllFailedEvents;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.AllMarkers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AtomFeedClientTest {

    private String feedLink = "feedLink";
    private URI feedUri;
    private AllFeeds allFeedsMock;
    private AllMarkers allMarkersMock;
    private AllFailedEvents allFailedEvents;
    private EventWorker eventWorker;
    private Entry entry1;
    private Entry entry2;

    @Before
    public void setUp() throws URISyntaxException {
        feedUri = new URI("http://myFeedUri");
        allFeedsMock = mock(AllFeeds.class);
        allMarkersMock = mock(AllMarkers.class);
        allFailedEvents = mock(AllFailedEvents.class);
        eventWorker = mock(EventWorker.class);
    }

    @Test
    public void shouldProcessEventsWithoutFailures() throws URISyntaxException {
        Feed feed = setupFeed();
        when(allFeedsMock.getFor(feedUri)).thenReturn(feed);
        when(allFailedEvents.getNumberOfFailedEvents(feedUri.toString())).thenReturn(0);

        FeedClient feedClient = new AtomFeedClient(allFeedsMock, allMarkersMock, allFailedEvents);
        feedClient.processEvents(feedUri, eventWorker);

        verify(eventWorker).process(argThat(new ArgumentMatcher<Event>() {
            @Override
            public boolean matches(Object o) {
                return ((Event) o).getId().equals(entry1.getId()) && ((Event) o).getFeedUri().equals(feedLink);
            }
        }));
        verify(allMarkersMock).put(feedUri, entry1.getId(), new URI(feedLink));
        verify(eventWorker).process(argThat(new ArgumentMatcher<Event>() {
            @Override
            public boolean matches(Object o) {
                return ((Event) o).getId().equals(entry2.getId());
            }
        }));
        verify(allMarkersMock).put(feedUri, entry2.getId(), new URI(feedLink));
    }

    @Test
    public void shouldProcessEventsAndNotUpdateMarkerIfFlagIsFalse() throws URISyntaxException {
        Feed feed = setupFeed();
        when(allFeedsMock.getFor(feedUri)).thenReturn(feed);
        when(allFailedEvents.getNumberOfFailedEvents(feedUri.toString())).thenReturn(0);

        FeedClient feedClient = new AtomFeedClient(allFeedsMock, allMarkersMock, allFailedEvents,false);
        feedClient.processEvents(feedUri, eventWorker);

        verify(eventWorker).process(argThat(new ArgumentMatcher<Event>() {
            @Override
            public boolean matches(Object o) {
                return ((Event) o).getId().equals(entry1.getId());
            }
        }));
        verify(allMarkersMock,Mockito.never()).put(feedUri, entry1.getId(), new URI(feedLink));
        verify(eventWorker).process(argThat(new ArgumentMatcher<Event>() {
            @Override
            public boolean matches(Object o) {
                return ((Event) o).getId().equals(entry2.getId());
            }
        }));
        verify(allMarkersMock, Mockito.never()).put(feedUri, entry2.getId(), new URI(feedLink));
    }


    @Test(expected = AtomFeedClientException.class)
    public void shouldNotProcessEventsIfThereAreTooManyFailedEvents() {
        when(allFailedEvents.getNumberOfFailedEvents(feedUri.toString())).thenReturn(50);

        FeedClient feedClient = new AtomFeedClient(allFeedsMock, allMarkersMock, allFailedEvents);
        feedClient.processEvents(feedUri, eventWorker);
    }

    @Test
    public void shouldHandleFailedEventInCaseProcessingFailsForAnEvent() throws URISyntaxException {
        Feed feed = setupFeed();
        when(allFeedsMock.getFor(feedUri)).thenReturn(feed);
        when(allFailedEvents.getNumberOfFailedEvents(feedUri.toString())).thenReturn(0);
        doThrow(Exception.class).when(eventWorker).process(any(Event.class));

        FeedClient feedClient = new AtomFeedClient(allFeedsMock, allMarkersMock, allFailedEvents);
        feedClient.processEvents(feedUri, eventWorker);

        ArgumentCaptor<FailedEvent> captor = ArgumentCaptor.forClass(FailedEvent.class);
        verify(allFailedEvents, times(2)).put(captor.capture());
        List<FailedEvent> failedEventList = captor.getAllValues();
        assertEquals(entry1.getId(), failedEventList.get(0).getEvent().getId());
        assertEquals(entry2.getId(), failedEventList.get(1).getEvent().getId());

        verify(allMarkersMock).put(feedUri, entry1.getId(), new URI(feedLink));
        verify(allMarkersMock).put(feedUri, entry2.getId(), new URI(feedLink));
    }

    @Test(expected = AtomFeedClientException.class)
    public void shouldStopProcessingEventsInBetweenWhenThereAreTooManyFailedEvents() throws URISyntaxException {
        Feed feed = setupFeed();
        when(allFeedsMock.getFor(feedUri)).thenReturn(feed);
        when(allFailedEvents.getNumberOfFailedEvents(feedUri.toString())).thenReturn(9, 9, 10);
        doThrow(Exception.class).when(eventWorker).process(any(Event.class));

        FeedClient feedClient = new AtomFeedClient(allFeedsMock, allMarkersMock, allFailedEvents);
        feedClient.processEvents(feedUri, eventWorker);

        ArgumentCaptor<FailedEvent> captor = ArgumentCaptor.forClass(FailedEvent.class);
        verify(allFailedEvents).put(captor.capture());
        assertEquals(entry1.getId(), captor.getValue().getEvent().getId());
        verify(allMarkersMock).put(feedUri, entry1.getId(), new URI(feedLink));
    }

    @Test
    public void shouldProcessFailedEvents() {
        List<FailedEvent> failedEvents = new ArrayList<FailedEvent>();
        Feed feed = setupFeed();
        failedEvents.add(new FailedEvent(feedUri.toString(), new Event(entry1), ""));
        failedEvents.add(new FailedEvent(feedUri.toString(), new Event(entry2), ""));
        when(allFailedEvents.getOldestNFailedEvents(feedUri.toString(), 5)).thenReturn(failedEvents);

        FeedClient feedClient = new AtomFeedClient(allFeedsMock, allMarkersMock, allFailedEvents);
        feedClient.processFailedEvents(feedUri, eventWorker);

        ArgumentCaptor<Event> eventArgumentCaptor = ArgumentCaptor.forClass(Event.class);
        ArgumentCaptor<FailedEvent> failedEventArgumentCaptor = ArgumentCaptor.forClass(FailedEvent.class);
        verify(eventWorker, times(2)).process(eventArgumentCaptor.capture());
        verify(allFailedEvents, times(2)).remove(failedEventArgumentCaptor.capture());

        List<Event> events = eventArgumentCaptor.getAllValues();
        assertEquals(entry1.getId(), events.get(0).getId());
        assertEquals(entry2.getId(), events.get(1).getId());

        List<FailedEvent> failedEventsCaptured = failedEventArgumentCaptor.getAllValues();
        assertEquals(entry1.getId(), failedEventsCaptured.get(0).getEvent().getId());
        assertEquals(entry2.getId(), failedEventsCaptured.get(1).getEvent().getId());
    }

    private Feed setupFeed() {
        entry1 = new Entry(); entry1.setId("id1");
        entry2 = new Entry(); entry2.setId("id2");
        return getFeed(entry1, entry2);
    }

    private Feed getFeed(Entry... entries) {
        ArrayList mutableEntries = new ArrayList();
        mutableEntries.addAll(Arrays.asList(entries));
        Feed feed = new Feed();
        feed.setOtherLinks(Arrays.asList(new Link[]{getLink("self", feedLink),getLink("via", feedLink)}));
        feed.setEntries(mutableEntries);
        return feed;
    }

    private Link getLink(String archiveType, String uri) {
        Link link = new Link();
        link.setRel(archiveType);
        link.setHref(uri);
        return link;
    }
}
