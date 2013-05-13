package org.ict4h.atomfeed.client.util;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UtilTest {

    private Feed feed;

    @Before
    public void setUp(){
        feed = new Feed();
        List alternateLinks = new ArrayList();

        Link selfLink = new Link();
        selfLink.setRel("self");
        selfLink.setHref("http://about.me");

        Link previousLink = new Link();
        previousLink.setRel("prev-archive");
        previousLink.setHref("http://prev.me");

        Link nextLink = new Link();
        nextLink.setRel("next-archive");
        nextLink.setHref("http://next.me");

        alternateLinks.add(selfLink);
        alternateLinks.add(previousLink);
        alternateLinks.add(nextLink);

        feed.setAlternateLinks(alternateLinks);
    }

    @Test
    public void shouldGetSelfLink() throws URISyntaxException {
        assertEquals(new URI("http://about.me"),Util.getSelfLink(feed));
    }

    @Test
    public void shouldGetPreviousLink() throws URISyntaxException {
        assertEquals(new URI("http://prev.me"),Util.getPreviousLink(feed));
    }

    @Test
    public void shouldGetNextLink() throws URISyntaxException {
        assertEquals(new URI("http://next.me"),Util.getNextLink(feed));
    }
}
