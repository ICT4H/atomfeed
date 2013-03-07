package org.openmrs.module.feedpublishermodule.event.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ict4htw.atomfeed.server.service.EventService;
import org.joda.time.DateTime;
import org.openmrs.DrugOrder;
import org.openmrs.OpenmrsObject;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.event.SubscribableEventListener;
import org.openmrs.module.feedpublishermodule.mapper.DosageRequestSerializer;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class DrugRegimenEventListener implements SubscribableEventListener {

    private EventService eventService;

//    @Autowired
    public DrugRegimenEventListener() {
//        this.eventService = eventService;
    }

    protected final Log logger = LogFactory.getLog(DrugRegimenEventListener.class);

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
            OrderService service = Context.getService(OrderService.class);
            Order order = service.getOrderByUuid(uuid);
            Patient patient = order.getPatient();
            logger.debug(String.format("patient found - %s", patient.getGivenName()));
            List<DrugOrder> drugOrdersForPatient = service.getDrugOrdersByPatient(patient, OrderService.ORDER_STATUS.CURRENT);
            List<org.ict4htw.atomfeed.server.service.Event> events = createEvents(drugOrdersForPatient);
//            eventService.notify(event);
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

    private List<org.ict4htw.atomfeed.server.service.Event> createEvents(List<DrugOrder> drugOrdersForPatient) throws URISyntaxException, IOException {
        ArrayList<org.ict4htw.atomfeed.server.service.Event> events = new ArrayList<org.ict4htw.atomfeed.server.service.Event>();
        DosageRequestSerializer dosageRequestSerializer = new DosageRequestSerializer();
        for(DrugOrder order : drugOrdersForPatient){
            //ignore implications of frequency for now.
            events.add(createEvent(dosageRequestSerializer.serialize(order)));
        }
        return events;
    }

    private org.ict4htw.atomfeed.server.service.Event createEvent(String dosageRequest) throws URISyntaxException {
        String contents = dosageRequest;
        String uuid = UUID.randomUUID().toString();
        String title = "";
        DateTime timeStamp = DateTime.now();
        String uriString = "";
        org.ict4htw.atomfeed.server.service.Event event = new org.ict4htw.atomfeed.server.service.Event(uuid,title, timeStamp, uriString,contents);
        return event;
    }

    //read from admin global properties.
    private void authenticate(){
        String username = "admin";
        String password =  "!4321Abcd";
        Context.authenticate(username, password);
    }
}
