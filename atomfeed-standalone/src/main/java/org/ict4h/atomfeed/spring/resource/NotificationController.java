package org.ict4h.atomfeed.spring.resource;

import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventFeedService;
import org.ict4h.atomfeed.server.service.EventService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URISyntaxException;
import java.util.UUID;

@Controller
public class NotificationController {

    EventFeedService eventFeedService;
    private final EventService eventService;

    @Autowired
    public NotificationController(EventFeedService eventFeedService, EventService eventService) {
        this.eventFeedService = eventFeedService;
        this.eventService = eventService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/entry/new")
    public String showForm(ModelMap model) {
        model.addAttribute("entry", new Entry());
        return "form";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/entry/new")
    public String addEntry(Entry entry, BindingResult result) throws URISyntaxException {
        Event event = new Event(
                UUID.randomUUID().toString(),
                entry.getTitle(),
                DateTime.now(),
                entry.getUrl(),
                entry.getContent());
        eventService.notify(event);
        return showForm(new ModelMap());
    }
}