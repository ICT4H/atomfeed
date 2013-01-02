package org.ict4htw.atomfeed.resource;

import com.sun.syndication.feed.atom.Feed;
import org.ict4htw.atomfeed.repository.AllEventRecords;
import org.ict4htw.atomfeed.service.EventFeedService;
import org.ict4htw.atomfeed.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class EventResource {
    private EventFeedService eventFeedService;

    @Autowired
    private AllEventRecords allEventRecords;

    @RequestMapping(method = RequestMethod.GET, value = "/events/recent")
    @ResponseBody
    public String getRecentEventFeed(HttpServletRequest httpServletRequest) {
        eventFeedService = new EventFeedService(httpServletRequest.getRequestURI(), allEventRecords);
        Feed feed = eventFeedService.getRecentFeed();
        return Util.stringifyFeed(feed);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/events/{startPos},{endPos}")
    @ResponseBody
    public String getEventFeed(HttpServletRequest httpServletRequest, @PathVariable int startPos, @PathVariable int endPos) {
        eventFeedService = new EventFeedService(httpServletRequest.getRequestURI(), allEventRecords);
        Feed feed = eventFeedService.getEventFeed(startPos, endPos);
        return Util.stringifyFeed(feed);
    }
}
