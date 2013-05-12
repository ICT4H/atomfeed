package org.ict4h.atomfeed.client.util;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class Util {

    public static URI getSelfLink(Feed feed) {
        for (Link link : (List<Link>) feed.getAlternateLinks()) {
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

    public static String getExceptionString(Exception e) {
        StringBuffer sb = null;
        try {
            sb = new StringBuffer();
            if (e.getMessage() != null) sb.append(e.getMessage());

            ByteArrayOutputStream stream = null;
            PrintStream ps = null;
            try {
                stream = new ByteArrayOutputStream();
                ps = new PrintStream(stream);
                e.printStackTrace(ps);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try { ps.close(); } catch(Exception e1) {}
            }

            sb.append(stream.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return sb.toString();
    }
}
