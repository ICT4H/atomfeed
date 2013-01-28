import org.ict4htw.atomfeed.server.service.Event;

import javax.jms.Message;

public interface MessageToEventMap {
    Event map(Message message);
}