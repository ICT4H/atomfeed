package org.openmrs.module.feedpublishermodule.event.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ict4htw.atomfeed.server.service.*;
import org.joda.time.DateTime;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.event.SubscribableEventListener;
import org.openmrs.serialization.OpenmrsSerializer;
import org.openmrs.serialization.SerializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

//@Service
public class DrugRegimenEventListener implements SubscribableEventListener {

    private EventService eventService;
    private OpenmrsSerializer serializer;

//    @Autowired
//    public DrugRegimenEventListener(EventService eventService,OpenmrsSerializer serializer) {
//        this.eventService = eventService;
//        this.serializer = serializer;
//    }

    protected final Log logger = LogFactory.getLog(DrugRegimenEventListener.class);

    @Override
    public List<Class<? extends OpenmrsObject>> subscribeToObjects() {
        Object classes = Arrays.asList(Patient.class);
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
            Patient patient = Context.getService(PatientService.class).getPatientByUuid(uuid);
            logger.info(String.format("patient found - %s", patient.getGivenName()));
            org.ict4htw.atomfeed.server.service.Event event = createEvent(patient);
            logger.info(String.format("event instantiated with contents - %s",event.getContents()));
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

    private org.ict4htw.atomfeed.server.service.Event createEvent(Patient patient) throws SerializationException, URISyntaxException {
        //new SimpleXStreamSerializer()
        String contents = new SimpleXStreamSerializer().serialize(patient);
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
