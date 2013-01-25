import org.ict4htw.atomfeed.server.service.EventService;
import org.openmrs.*;
import org.openmrs.event.Event;
import org.openmrs.event.SubscribableEventListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import java.util.Arrays;
import java.util.List;

@Component
public class AtomFeedEventListener implements SubscribableEventListener {

    EventService eventService;
    private MessageToEventMap messageToEventMap;

    AtomFeedEventListener(EventService eventService, MessageToEventMap messageToEventMap){
        this.eventService=eventService;
        this.messageToEventMap = messageToEventMap;
    }

    @Override
    public List<Class<? extends org.openmrs.OpenmrsObject>> subscribeToObjects() {
        Object classes = Arrays.asList(Patient.class, Concept.class, Encounter.class, Obs.class);
        return (List<Class<? extends OpenmrsObject>>) classes;
    }

    @Override
    public List<String> subscribeToActions() {
        return Arrays.asList(Event.Action.CREATED.name(), Event.Action.UPDATED.name(), Event.Action.VOIDED.name(), Event.Action.PURGED.name());
    }

    @Override
    public void onMessage(Message message) {
        eventService.notify(messageToEventMap.map(message));
    }

}
