package org.ict4htw.atomfeed.demo;

import org.ict4htw.atomfeed.MessageToEventMap;
import org.ict4htw.atomfeed.server.service.Event;
import org.junit.Test;
import org.openmrs.OpenmrsObject;

import javax.jms.MapMessage;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: karthik
 * Date: 28/01/13
 * Time: 4:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageToEventMapImplTest{



    @Test
    public void testMap() throws Exception {
        AtomFeedService atomFeedService = mock(AtomFeedService.class);
        OpenmrsObject testObject = mock(OpenmrsObject.class);
        when(atomFeedService.getObjectByUuid("mockedClassName","mockedUUId")).thenReturn(testObject);

        MessageToEventMap messageToEventMap = new MessageToEventMapImpl(atomFeedService);

        MapMessage mapMessage=mock(MapMessage.class);
        when(mapMessage.getString("action")).thenReturn("mockedAction");
        when(mapMessage.getString("classname")).thenReturn("mockedClassName");
        when(mapMessage.getString("uuid")).thenReturn("mockedUUId");

        Event map = messageToEventMap.map(mapMessage);
        assertNotNull(map);
        assertNotNull(map.getEventObject());
        assertEquals(map.getEventObject(), testObject);

    }
}
