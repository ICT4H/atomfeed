package org.ict4h.atomfeed.client.util;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class FeedUtil {

    public static URI getSelfLink(Feed feed) {
        for (Link link : (List<Link>)feed.getAlternateLinks()) {
            if (link.getRel().equalsIgnoreCase("self")) {
                try {
                    return new URI(link.getHref());
                } catch (URISyntaxException e) {
                    throw new RuntimeException("Bad link in self");
                }
            }
        }
        throw new RuntimeException("Self link not found.");
    }

}
