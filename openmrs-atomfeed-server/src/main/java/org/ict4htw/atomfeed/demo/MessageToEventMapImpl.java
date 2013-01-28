package org.ict4htw.atomfeed.demo;

import org.ict4htw.atomfeed.MessageToEventMap;
import org.ict4htw.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openmrs.OpenmrsObject;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import java.net.URISyntaxException;

public class MessageToEventMapImpl implements MessageToEventMap{

    private AtomFeedService atomFeedService;

    public MessageToEventMapImpl(AtomFeedService atomFeedService) {
        this.atomFeedService = atomFeedService;
    }

    @Override
    public Event map(Message message) throws URISyntaxException {
        MapMessage msg = (MapMessage) message;
        String action = null;
        String classname = null;
        String uuid = null;
        try {
            action = msg.getString("action");
            classname = msg.getString("classname");
            uuid = msg.getString("uuid");
        } catch (JMSException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        OpenmrsObject openmrsObject = atomFeedService.getObjectByUuid(classname, uuid);
        return new Event(uuid, "atomfeedServerEvent."+action, new DateTime(), "http://url/"+uuid, openmrsObject);
    }
}
