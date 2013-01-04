package org.ict4htw.atomfeed.server.resource;

import com.sun.syndication.feed.atom.Feed;
import org.ict4htw.atomfeed.server.service.EventFeedService;
import org.ict4htw.atomfeed.server.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

@Controller
public class EventResource {
    private EventFeedService eventFeedService;

    @Autowired
    public EventResource(EventFeedService eventFeedService) {
        this.eventFeedService = eventFeedService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/events/recent")
    @ResponseBody
    public String getRecentEventFeed(HttpServletRequest httpServletRequest) {
        try {
            Feed feed = eventFeedService.getRecentFeed(new URI(httpServletRequest.getRequestURI()));
            return Util.stringifyFeed(feed);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Bad URI", e);
        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "/events/{startPos},{endPos}")
    @ResponseBody
    public String getEventFeed(HttpServletRequest httpServletRequest, @PathVariable int startPos, @PathVariable int endPos) {
        try {
            Feed feed = eventFeedService.getEventFeed(startPos, endPos, new URI(httpServletRequest.getRequestURI()));
            return Util.stringifyFeed(feed);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Bad URI", e);
        }
    }
}