package org.ict4htw.atomfeed.server.util;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.WireFeedOutput;
import com.thoughtworks.xstream.XStream;
import org.ict4htw.atomfeed.server.exceptions.AtomFeedRuntimeException;

import java.io.StringWriter;

public class Util {

    public static String stringify(Object payload) {
        XStream serializer = new XStream();
        return serializer.toXML(payload);
    }

    public static String stringifyFeed(Feed feed) {
        try {
            WireFeedOutput output = new WireFeedOutput();
            final StringWriter stringWriter = new StringWriter();
            output.output(feed, stringWriter, true);
            return stringWriter.toString();
        } catch (Exception e) {
            throw new AtomFeedRuntimeException(e);
        }
    }

}
