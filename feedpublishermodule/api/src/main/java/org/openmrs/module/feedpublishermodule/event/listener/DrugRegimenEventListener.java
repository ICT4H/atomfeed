package org.openmrs.module.feedpublishermodule.event.listener;

import org.apache.activemq.command.ActiveMQMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.event.SubscribableEventListener;
import org.openmrs.serialization.OpenmrsSerializer;
import org.openmrs.serialization.SimpleXStreamSerializer;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import java.util.Arrays;
import java.util.List;

public class DrugRegimenEventListener implements SubscribableEventListener {

    public DrugRegimenEventListener() {
    }

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
            String contents = new SimpleXStreamSerializer().serialize(patient);
            logger.info(String.format("patient found - %s", contents));
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

    private void authenticate(){
        String username = "admin";
        String password =  "!4321Abcd";
        Context.authenticate(username, password);
    }
}
