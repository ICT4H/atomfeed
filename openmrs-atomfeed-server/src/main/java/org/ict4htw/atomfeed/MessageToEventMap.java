package org.ict4htw.atomfeed;

import org.ict4htw.atomfeed.server.service.Event;

import javax.jms.Message;
import java.net.URISyntaxException;

public interface MessageToEventMap {
    Event map(Message message) throws URISyntaxException;
}