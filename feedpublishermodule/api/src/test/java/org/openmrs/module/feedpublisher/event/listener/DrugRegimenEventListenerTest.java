package org.openmrs.module.feedpublisher.event.listener;


import org.apache.activemq.command.ActiveMQMapMessage;
import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.api.OrderService;
import org.openmrs.module.feedpublishermodule.context.ContextWrapper;
import org.openmrs.module.feedpublishermodule.event.listener.DrugRegimenEventListener;
import org.openmrs.module.feedpublishermodule.mapper.DrugOrderSerializer;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Date;

import static org.mockito.Mockito.*;

public class DrugRegimenEventListenerTest {
    private ContextWrapper context;
    private EventService eventService;
    private DrugOrderSerializer serializer;

    @Before
    public void setUp(){
        context = mock(ContextWrapper.class);
        serializer = mock(DrugOrderSerializer.class);
        eventService = mock(EventService.class);
    }

    //Too Big an interaction test. Introduce better abstractions
    @Test
    public void shouldNotifyAnEventWhenADrugRegimenIsCreated() throws Exception {
        String uuid = "42";
        DrugRegimenEventListener listener = new DrugRegimenEventListener(eventService, serializer, context);
        OrderService service = Mockito.mock(OrderService.class);
        when(context.getService(OrderService.class)).thenReturn(service);
        DrugOrder order = createDrugOrder();
        when(service.getOrderByUuid(uuid)).thenReturn(order);
        Event event = new Event("","",null,"","");
        when(serializer.asEvent(order)).thenReturn(event);

        listener.onMessage(createMessage(uuid));

        verify(eventService).notify(event);
    }

    private DrugOrder createDrugOrder() {
        DrugOrder order = new DrugOrder();
        order.setStartDate(new Date("01/01/2013"));
        order.setFrequency("2/day x 7 days/week");
        order.setDrug(new Drug());
        return order;
    }

    private MapMessage createMessage(String uuid) throws JMSException {
        ActiveMQMapMessage message = new ActiveMQMapMessage();
        message.setObject("uuid",uuid);
        return message;
    }
}
