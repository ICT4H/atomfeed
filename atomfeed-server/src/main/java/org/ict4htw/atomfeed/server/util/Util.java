package org.ict4htw.atomfeed.server.util;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;
import com.thoughtworks.xstream.XStream;

public class Util {

    public static String stringify(Object payload) {
        return new XStream().toXML(payload);
    }

    public static String stringifyFeed(Feed feed) throws FeedException {
        return new WireFeedOutput().outputString(feed);
    }

}
