package org.openmrs.module.feedpublishermodule.event.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ict4htw.atomfeed.server.service.EventService;
import org.openmrs.DrugOrder;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.event.SubscribableEventListener;
import org.openmrs.module.feedpublishermodule.mapper.DrugOrderSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import java.util.Arrays;
import java.util.List;

@Component
public class DrugRegimenEventListener implements SubscribableEventListener {

    private EventService eventService;
    private DrugOrderSerializer serializer;
    protected final Log logger = LogFactory.getLog(DrugRegimenEventListener.class);

    @Autowired
    public DrugRegimenEventListener(EventService eventService) {
        this.eventService = eventService;
        //TODO: AutoWire
        this.serializer = new DrugOrderSerializer();
    }

    @Override
    public List<Class<? extends OpenmrsObject>> subscribeToObjects() {
        Object classes = Arrays.asList(DrugOrder.class);
        return (List<Class<? extends OpenmrsObject>>) classes;
    }

    @Override
    public List<String> subscribeToActions() {
        return Arrays.asList(Event.Action.CREATED.name());
    }

    @Override
    public void onMessage(Message message){
        try{
            Context.openSession();
            authenticate();
            String uuid = ((MapMessage) message).getString("uuid");
            //Wrap these in another class. Create a Context Wrapper to facilitate easier testing
            DrugOrder order = (DrugOrder) Context.getService(OrderService.class).getOrderByUuid(uuid);
            //ignore implications of frequency for now. Just Publish one event.
            eventService.notify(serializer.asEvent(order));
        }
        catch (JMSException jmsException){
            logger.error("Jms Exception raised");
            jmsException.printStackTrace();
        }
        catch (Exception ex){
            logger.error("Error while processing feed");
            ex.printStackTrace();
        }
        finally {
            Context.closeSession();
        }
    }

    //TODO: read from admin global properties.
    private void authenticate(){
        String username = "admin";
        String password =  "!4321Abcd";
        Context.authenticate(username, password);
    }
}
