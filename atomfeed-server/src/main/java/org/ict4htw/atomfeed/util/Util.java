package org.ict4htw.atomfeed.util;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.WireFeedOutput;
import org.postgresql.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringWriter;

public class Util {

    public static String stringify(Object object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // TODO: specify proper encoding here
        return Base64.encodeBytes(baos.toByteArray());
//        return new String(baos.toByteArray());
    }

    public static String stringifyFeed(Feed feed) {
        try {
            WireFeedOutput output = new WireFeedOutput();
            final StringWriter stringWriter = new StringWriter();
            output.output(feed, stringWriter, true);
            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
