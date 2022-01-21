package org.ict4h.atomfeed.server.service.helper;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;
import org.apache.logging.log4j.Logger;
import org.ict4h.atomfeed.server.service.EventFeedService;
import org.ict4h.atomfeed.transaction.AFTransactionManager;
import org.ict4h.atomfeed.transaction.AFTransactionWork;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A Helper class that can be used to generate a {@code String} representation of a {@code Feed}.
 */
public class EventFeedServiceHelper {
    public static String getRecentFeed(final EventFeedService eventFeedService, final String requestURL, final String category, Logger logger, AFTransactionManager atomTxManager){
        try {
            final URI requestUri = new URI(requestURL);
            Feed feed = atomTxManager.executeWithTransaction(new AFTransactionWork<Feed>() {
                @Override
                public Feed execute() {
                    return eventFeedService.getRecentFeed(requestUri,category);
                }
                @Override
                public PropagationDefinition getTxPropagationDefinition() {
                    return PropagationDefinition.PROPAGATION_REQUIRED;
                }
            });
            return new WireFeedOutput().outputString(feed);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Bad URI", e);
        } catch (Exception e) {
            logger.error("error occurred while getting recent feedgenerator", e);
            //TODO: should throw exception that should be either contextual error like bad request
            //to be resolved by an exception resolver to  return error code 400 or so
            throw new RuntimeException("Unexpected error", e);
        }
    }

    public static String getEventFeed(final EventFeedService eventFeedService,
                                      String requestURL, final String category, final int feedNumber, Logger logger, AFTransactionManager atomTxManager){
        try {
            final URI requestUri = new URI(requestURL);
            Feed feed = atomTxManager.executeWithTransaction(new AFTransactionWork<Feed>() {
                @Override
                public Feed execute() {
                    return eventFeedService.getEventFeed(requestUri, category, feedNumber);
                }
                @Override
                public PropagationDefinition getTxPropagationDefinition() {
                    return PropagationDefinition.PROPAGATION_REQUIRED;
                }
            });
            return new WireFeedOutput().outputString(feed);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Bad URI", e);
        } catch (FeedException e) {
            logger.error("error occurred while getting recent feedgenerator", e);
            throw new RuntimeException("Error serializing feed.", e);
        }
    }
}
