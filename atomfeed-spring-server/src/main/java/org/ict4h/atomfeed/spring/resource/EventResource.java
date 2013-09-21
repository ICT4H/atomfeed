package org.ict4h.atomfeed.spring.resource;

import org.apache.log4j.Logger;
import org.ict4h.atomfeed.server.domain.criterion.CategoryTitleCriterion;
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
        return EventFeedServiceHelper.getRecentFeed(eventFeedService,httpServletRequest.getRequestURL().toString(), null, logger);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/feed/{category}/recent", produces = "application/atom+xml")
    @ResponseBody
    public String getRecentEventFeedForCategory(HttpServletRequest httpServletRequest,@PathVariable String category) {
        return EventFeedServiceHelper.getRecentFeed(eventFeedService,httpServletRequest.getRequestURL().toString(),
                                                    category, logger);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/feed/{n}")
    @ResponseBody
    public String getEventFeed(HttpServletRequest httpServletRequest, @PathVariable int n) {
        return EventFeedServiceHelper.getEventFeed(
                eventFeedService,httpServletRequest.getRequestURL().toString(),null, n,logger);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/feed/{category}/{n}")
    @ResponseBody
    public String getEventFeedWithCategory(HttpServletRequest httpServletRequest,
                                           @PathVariable String category,  @PathVariable int n) {
        return EventFeedServiceHelper.getEventFeed(eventFeedService,httpServletRequest.getRequestURL().toString(),
                                                   category, n,logger);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/feed/{category}/{title}/recent")
    @ResponseBody
    public String getRecentEventFeedWithCategoryAndTitle(HttpServletRequest httpServletRequest,
                                           @PathVariable String category,  @PathVariable String title) {
        return EventFeedServiceHelper.getRecentFeed(new CategoryTitleCriterion(category,title),eventFeedService, httpServletRequest.getRequestURL().toString(), logger);
    }
}