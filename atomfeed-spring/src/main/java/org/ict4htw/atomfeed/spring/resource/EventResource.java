package org.ict4htw.atomfeed.spring.resource;

import com.sun.syndication.feed.atom.Feed;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;
import org.apache.log4j.Logger;
import org.ict4htw.atomfeed.server.service.Event;
import org.ict4htw.atomfeed.server.service.EventFeedService;
import org.ict4htw.atomfeed.server.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(method = RequestMethod.GET, value = "/events/recent")
    @ResponseBody
    public String getRecentEventFeed(HttpServletRequest httpServletRequest) {
        try {
            Feed feed = eventFeedService.getRecentFeed(new URI(httpServletRequest.getRequestURI()));
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
            Feed feed = eventFeedService.getEventFeed(new URI(httpServletRequest.getRequestURI()), feedId);
            return new WireFeedOutput().outputString(feed);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Bad URI", e);
        } catch (FeedException e) {
            throw new RuntimeException("Error serializing feed.", e);
        }
        //TODO: check comments in getRecentFeed()
    }


    @RequestMapping(method = RequestMethod.POST, value = "/events")
    public AtomResponse notifyEvent(@ModelAttribute Event event){
        //TODO Security !!
        eventService.notify(event);
        return new AtomResponse(AtomResponse.ResponseStatus.SUCCESS);
    }
}