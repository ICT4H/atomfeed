/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.feedpublishermodule.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ict4htw.atomfeed.server.service.EventFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class FeedController {

    protected final Log logger = LogFactory.getLog(FeedController.class);
    private EventFeedService feedService;

    @Autowired
    public FeedController(EventFeedService eventFeedService) {
        this.feedService = eventFeedService;
    }

    @RequestMapping(value = "/module/feedpublishermodule/events/recent", method = RequestMethod.GET)
    @ResponseBody
	public ResponseEntity<String> get(HttpServletRequest request){
        return new ResponseEntity<String>("{\"feeds\" : 42}",HttpStatus.OK);
//        try {
//            Feed feed = feedService.getRecentFeed(new URI(request.getRequestURL().toString()));
//            String output = new WireFeedOutput().outputString(feed);
//            return new ResponseEntity<String>(output,HttpStatus.OK);
//        } catch (Exception e) {
//            logger.error("error occurred while getting recent feeds", e);
//            throw new RuntimeException("Unexpected error", e);
//        }
	}
}
