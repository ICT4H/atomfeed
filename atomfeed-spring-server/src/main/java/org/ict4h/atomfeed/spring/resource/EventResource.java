package org.ict4h.atomfeed.spring.resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ict4h.atomfeed.server.service.EventFeedService;
import org.ict4h.atomfeed.server.service.helper.EventFeedServiceHelper;
import org.ict4h.atomfeed.server.transaction.AtomFeedSpringTransactionSupport;
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
    private AtomFeedSpringTransactionSupport atomTxManager;
    private static Logger logger = LogManager.getLogger(EventResource.class);

    @Autowired
    public EventResource(EventFeedService eventFeedService, AtomFeedSpringTransactionSupport atomTxManager) {
        this.eventFeedService = eventFeedService;
        this.atomTxManager = atomTxManager;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/feed/recent", produces = "application/atom+xml")
    @ResponseBody
    public String getRecentEventFeed(HttpServletRequest httpServletRequest) {
        return EventFeedServiceHelper.getRecentFeed(eventFeedService,httpServletRequest.getRequestURL().toString(), null, logger, atomTxManager);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/feed/{category}/recent", produces = "application/atom+xml")
    @ResponseBody
    public String getRecentEventFeedForCategory(HttpServletRequest httpServletRequest,@PathVariable String category) {
        return EventFeedServiceHelper.getRecentFeed(eventFeedService,httpServletRequest.getRequestURL().toString(),
                                                    category, logger, atomTxManager);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/feed/{feedNumber}")
    @ResponseBody
    public String getEventFeed(HttpServletRequest httpServletRequest, @PathVariable int feedNumber) {
        return EventFeedServiceHelper.getEventFeed(
                eventFeedService,httpServletRequest.getRequestURL().toString(),null, feedNumber,logger, atomTxManager);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/feed/{category}/{feedNumber}")
    @ResponseBody
    public String getEventFeedWithCategory(HttpServletRequest httpServletRequest,
                                           @PathVariable String category,  @PathVariable int feedNumber) {
        return EventFeedServiceHelper.getEventFeed(eventFeedService,httpServletRequest.getRequestURL().toString(),
                                                   category, feedNumber,logger, atomTxManager);
    }
}