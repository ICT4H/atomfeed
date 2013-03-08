package org.openmrs.module.feedpublishermodule.event.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ict4htw.atomfeed.server.service.EventService;
import org.openmrs.DrugOrder;
import org.openmrs.OpenmrsObject;
import org.openmrs.Order;
import org.openmrs.Patient;
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
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DrugRegimenEventListener implements SubscribableEventListener {

    private EventService eventService;
    protected final Log logger = LogFactory.getLog(DrugRegimenEventListener.class);

    @Autowired
    public DrugRegimenEventListener(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public List<Class<? extends OpenmrsObject>> subscribeToObjects() {
        Object classes = Arrays.asList(DrugOrder.class);
        return (List<Class<? extends OpenmrsObject>>) classes;
    }

    @Override
    public List<String> subscribeToActions() {
        return Arrays.asList(Event.Action.CREATED.name(), Event.Action.UPDATED.name());
    }

    @Override
    public void onMessage(Message message){
        try{
            Context.openSession();
            authenticate();
            String uuid = ((MapMessage) message).getString("uuid");
            //Wrap these in another class. Create a Context Wrapper to facilitate easier testing
            OrderService service = Context.getService(OrderService.class);
            Order order = service.getOrderByUuid(uuid);
            Patient patient = order.getPatient();
            logger.debug(String.format("patient found - %s", patient.getGivenName()));
            List<DrugOrder> drugOrdersForPatient = service.getDrugOrdersByPatient(patient, OrderService.ORDER_STATUS.CURRENT);
            publishAll(createEvents(drugOrdersForPatient));
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

    private void publishAll(List<org.ict4htw.atomfeed.server.service.Event> events) {
        for (org.ict4htw.atomfeed.server.service.Event event : events){
            eventService.notify(event);
        }
    }

    private List<org.ict4htw.atomfeed.server.service.Event> createEvents(List<DrugOrder> drugOrdersForPatient) throws URISyntaxException, IOException {
        ArrayList<org.ict4htw.atomfeed.server.service.Event> events = new ArrayList<org.ict4htw.atomfeed.server.service.Event>();
        //Move To constructor
        DrugOrderSerializer drugOrderSerializer = new DrugOrderSerializer();
        for(DrugOrder order : drugOrdersForPatient){
            //ignore implications of frequency for now.
            events.add(drugOrderSerializer.asEvent(order));
        }
        return events;
    }

    //read from admin global properties.
    private void authenticate(){
        String username = "admin";
        String password =  "!4321Abcd";
        Context.authenticate(username, password);
    }
}
