package org.ict4h.atomfeed.spring.resource;

import org.apache.log4j.Logger;
import org.ict4h.atomfeed.server.service.EventFeedService;
import org.ict4h.atomfeed.server.service.helper.EventFeedServiceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class EventResource {
    private EventFeedService eventFeedService;
    private static Logger logger = Logger.getLogger(EventResource.class);

    @Autowired
    public EventResource(EventFeedService eventFeedService) {
        this.eventFeedService = eventFeedService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/feed/recent", produces = "application/atom+xml")
    @ResponseBody
    public String getRecentEventFeed(HttpServletRequest httpServletRequest) {
        return EventFeedServiceHelper.getRecentFeed(eventFeedService,httpServletRequest.getRequestURL().toString(),logger);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/feed/{n}")
    @ResponseBody
    public String getEventFeed(HttpServletRequest httpServletRequest, @PathVariable int n) {
        return EventFeedServiceHelper.getEventFeed(
                eventFeedService,httpServletRequest.getRequestURL().toString(),
                null, n,logger);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/feed/{category}/{n}")
    @ResponseBody
    public String getEventFeedWithCategory(HttpServletRequest httpServletRequest,
                                           @PathVariable String category,  @PathVariable int n) {
        return EventFeedServiceHelper.getEventFeed(
                eventFeedService,httpServletRequest.getRequestURL().toString(),
                category, n,logger);
    }
}