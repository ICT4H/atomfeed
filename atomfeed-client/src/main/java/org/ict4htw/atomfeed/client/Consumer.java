package org.ict4htw.atomfeed.client;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

//import com.sun.syndication.feed.WireFeed;
//import com.sun.syndication.feed.atom.Entry;
//import com.sun.syndication.feed.atom.Feed;
//import com.sun.syndication.feed.atom.Link;
//import com.sun.syndication.io.WireFeedInput;

public class Consumer {
//    private static final String ATOM_MEDIA_TYPE = "application/atom+xml";
//
//    public static void main(String[] args) throws Exception {
//        URI feedUri = processCommandLineArgs(args);
//
//        Consumer consumer = new Consumer();
//
//        feedUri = consumer.navigateBackwardThroughFeeds(feedUri);
//        System.out.println(String.format("Finished navigating backwards through feeds, terminated at [%s]", feedUri.toString()));
//
//        feedUri = consumer.navigateForwardThroughFeeds(feedUri);
//        System.out.println(String.format("Finished navigating forwards through feeds, terminated at [%s]", feedUri.toString()));
//
//    }
//
//    private URI navigateForwardThroughFeeds(URI feedUri) throws URISyntaxException {
//        Feed feed = getFeed(feedUri);
//        processEvents(feed.getEntries(), feedUri);
//
//        while (hasNextArchive(feed)) {
//            feedUri = getNextArchive(feed);
//            feed = getFeed(feedUri);
//            processEvents(feed.getEntries(), feedUri);
//        }
//        return feedUri;
//    }
//
//    private URI navigateBackwardThroughFeeds(URI feedUri) throws URISyntaxException {
//        Feed feed = getFeed(feedUri);
//        processEvents(feed.getEntries(), feedUri);
//
//        while (hasPrevArchive(feed)) {
//            feedUri = getPrevArchive(feed);
//            feed = getFeed(feedUri);
//            processEvents(feed.getEntries(), feedUri);
//        }
//        return feedUri;
//    }
//
//    private boolean hasPrevArchive(Feed feed) throws URISyntaxException {
//        return getPrevArchive(feed) != null;
//    }
//
//    private boolean hasNextArchive(Feed feed) throws URISyntaxException {
//        return getNextArchive(feed) != null;
//    }
//
//    private static URI processCommandLineArgs(String[] args) throws URISyntaxException {
//        if(args.length != 1) {
//            System.out.println("Must specify entry point URI as the only command line argument ");
//            System.exit(1);
//        } else {
//            System.out.println("Binding to service at: " + args[0]);
//        }
//        return new URI(args[0]);
//    }
//
//    private URI getUriFromNamedLink(String relValue, Feed feed) throws URISyntaxException {
//        for (Object obj : feed.getOtherLinks()) {
//            Link l = (Link) obj;
//            if (l.getRel().equals(relValue)) {
//                return new URI(l.getHref());
//            }
//        }
//        return null;
//    }
//
//    private URI getPrevArchive(Feed feed) throws URISyntaxException {
//        return getUriFromNamedLink("prev-archive", feed);
//    }
//
//    private URI getNextArchive(Feed feed) throws URISyntaxException {
//        return getUriFromNamedLink("next-archive", feed);
//    }
//
//    private void processEvents(List<Entry> entries, URI feedUri) {
//        System.out.println(String.format("Processed [%d] entries from feed with URI [%s]", entries.size(), feedUri.toString()));
//    }
//
//    private Feed getFeed(URI uri) {
//        Client client = Client.create();
//        ClientResponse response = client.resource(uri).accept(ATOM_MEDIA_TYPE).get(ClientResponse.class);
//
//        String responseString = response.getEntity(String.class);
//
//        //some UTF-8 encoded files include a three-byte UTF-8 Byte-order mark
//        //strip this off (otherwise we get 'org.xml.sax.SAXParseException: Content is not allowed in prolog')
//        responseString = responseString.trim().replaceFirst("^([\\W]+)<","<");
//
//        WireFeedInput wfi = new WireFeedInput();
//        WireFeed wireFeed;
//        try {
//            wireFeed = wfi.build(new StringReader(responseString));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        return (Feed) wireFeed;
//    }
}
