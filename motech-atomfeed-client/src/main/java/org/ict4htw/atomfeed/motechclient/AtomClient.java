package org.ict4htw.atomfeed.motechclient;

import com.sun.syndication.feed.atom.Entry;
import org.ict4htw.atomfeed.client.FeedEnumerator;
import org.ict4htw.atomfeed.client.repository.AllFeeds;
import org.ict4htw.atomfeed.client.repository.datasource.WebClient;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Component
public class AtomClient {

    //should be persisted
    private String lastRecordId;
    private EventToMotechEventMapper eventToMotechEventMapper;

    public EventRelay getEventRelay() {
        return eventRelay;
    }

    public void setEventRelay(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    @Autowired
    private EventRelay eventRelay;
    private FeedEnumerator feedEnumerator;


    public AtomClient(String startingURL, WebClient webClient, EventToMotechEventMapper eventToMotechEventMapper) throws URISyntaxException {
        this.eventToMotechEventMapper = eventToMotechEventMapper;
        AllFeeds allFeeds = new AllFeeds(webClient);
        feedEnumerator = new FeedEnumerator(allFeeds, new URI(startingURL));

    }


    public  void update() throws URISyntaxException {
        List<Entry> entries=null;
        if(lastRecordId==null) {
            entries=feedEnumerator.getAllEntries();
        }
        else
            entries=feedEnumerator.newerEntries(lastRecordId);
        for (Entry entry : entries) {
            //need to know if they need any change
            MotechEvent event = eventToMotechEventMapper.map(entry);
            eventRelay.sendEventMessage(event);
        }
        setLastRecordId(entries);
    }


    //Presist
    private void setLastRecordId(List<Entry> entries) {
        lastRecordId=entries.get(entries.size()-1).getId();
    }

}
