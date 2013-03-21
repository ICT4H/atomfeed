package org.ict4h.atomfeed.spring.resource;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;
import org.apache.log4j.Logger;
import org.ict4h.atomfeed.server.service.EventFeedService;
import org.ict4h.atomfeed.server.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

@Controller
public class EventResource {
    private EventFeedService eventFeedService;
    private EventService eventService;
    private static Logger logger = Logger.getLogger(EventResource.class);

    @Autowired
    public EventResource(EventFeedService eventFeedService, EventService eventService) {
        this.eventFeedService = eventFeedService;
        this.eventService = eventService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/events/recent", produces = "application/atom+xml")
    @ResponseBody
    public String getRecentEventFeed(HttpServletRequest httpServletRequest) {
    	try {
            Feed feed = eventFeedService.getRecentFeed(new URI(httpServletRequest.getRequestURL().toString()));
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

    @RequestMapping(method = RequestMethod.GET, value = "/events/{feedId}")
    @ResponseBody
    public String getEventFeed(HttpServletRequest httpServletRequest, @PathVariable int feedId) {
    	try {
            Feed feed = eventFeedService.getEventFeed(new URI(httpServletRequest.getRequestURL().toString()), feedId);
            return new WireFeedOutput().outputString(feed);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Bad URI", e);
        } catch (FeedException e) {
            throw new RuntimeException("Error serializing feed.", e);
        }
        //TODO: check comments in getRecentFeed()
    }
}