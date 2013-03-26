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

import org.apache.log4j.Logger;
import org.ict4h.atomfeed.server.service.EventFeedService;
import org.ict4h.atomfeed.server.service.helper.EventFeedServiceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class FeedController {
    protected final Logger logger = Logger.getLogger(FeedController.class);
    private EventFeedService eventFeedService;

    @Autowired
    public FeedController(EventFeedService eventFeedService) {
        this.eventFeedService = eventFeedService;

    }

    @RequestMapping(value = "feed/recent", method = RequestMethod.GET)
    @ResponseBody
	public ResponseEntity<String> getRecentFeed(HttpServletRequest request){
        String response = EventFeedServiceHelper.getRecentFeed(eventFeedService,request.getRequestURL().toString(),logger);
        return respondWithHeaders(response);
	}

    @RequestMapping( value = "/feed/{n}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getEventFeed(HttpServletRequest httpServletRequest, @PathVariable int n) {
        String response = EventFeedServiceHelper.getEventFeed(eventFeedService, httpServletRequest.getRequestURL().toString(), n, logger);
        return respondWithHeaders(response);
    }

    private ResponseEntity<String> respondWithHeaders(String response) {
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type", "application/atom+xml");
        return new ResponseEntity<String>(response, headers, HttpStatus.OK);
    }
}
