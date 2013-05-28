package org.ict4h.atomfeed.server.service.helper;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;
import org.apache.log4j.Logger;
import org.ict4h.atomfeed.server.service.EventFeedService;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A Helper class that can be used to generate a {@code String} representation of a {@code Feed}.
 */
public class EventFeedServiceHelper {
    public static String getRecentFeed(EventFeedService eventFeedService, String requestURL, Logger logger ){
        try {
            Feed feed = eventFeedService.getRecentFeed(new URI(requestURL));
            return new WireFeedOutput().outputString(feed);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Bad URI", e);
        } catch (Exception e) {
            logger.error("error occurred while getting recent feedgenerator", e);
            //TODO: should throw exception that should be either contextual error like bad request
            //to be resolved by an exception resolver to  return error code 400 or so
            throw new RuntimeException("Unexpected error", e); //TODO
        }
    }

    public static String getEventFeed(EventFeedService eventFeedService,
                                      String requestURL, String category, int feedNumber, Logger logger){
        try {
            Feed feed = eventFeedService.getEventFeed(new URI(requestURL), null, feedNumber);
            return new WireFeedOutput().outputString(feed);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Bad URI", e);
        } catch (FeedException e) {
            logger.error("error occurred while getting recent feedgenerator", e);
            throw new RuntimeException("Error serializing feed.", e);
        }
        //TODO: check comments in getRecentFeed()
    }
}
