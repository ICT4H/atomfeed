package org.ict4htw.atomfeed.server.domain;

import org.ict4htw.atomfeed.client.api.data.Event;
import org.ict4htw.atomfeed.client.repository.datasource.WebClient;
import org.ict4htw.atomfeed.motechclient.AtomClient;
import org.ict4htw.atomfeed.motechclient.EventToMotechEventMapper;
import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistory;
import org.ict4htw.atomfeed.server.repository.AllEventRecordsStub;
import org.ict4htw.atomfeed.server.repository.InMemoryEventRecordCreator;
import org.ict4htw.atomfeed.server.service.EventFeedService;
import org.ict4htw.atomfeed.server.service.EventService;
import org.ict4htw.atomfeed.server.service.feedgenerator.NumberFeedGenerator;
import org.ict4htw.atomfeed.spring.resource.EventResource;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;

import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.*;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath:applicationContext.xml")
public class AtomClientTest {

    AtomClient atomClient;
    private InMemoryEventRecordCreator feedRecordCreator;

    @Before
    public void setup() throws URISyntaxException {
        AllEventRecordsStub allEventRecords = new AllEventRecordsStub();
        feedRecordCreator = new InMemoryEventRecordCreator(allEventRecords);
        EventService eventService = new EventService(allEventRecords);
        NumberBasedChunkingHistory numberBasedChunkingHistory=new NumberBasedChunkingHistory();
        EventFeedService eventFeedService = new EventFeedService(new NumberFeedGenerator(allEventRecords, numberBasedChunkingHistory));
        WebClient webClientStub = new WebClientStub(new EventResource(eventFeedService, eventService));
        EventToMotechEventMapper eventMapperEventTo =new EventToMotechEventMapper(){
            @Override
            public MotechEvent map(Event event) {
                MotechEvent motechEvent = new MotechEvent("com.atomfeed.entry."+event.getId());
                return motechEvent;
            }
        };
        atomClient=new AtomClient(new URI("http://foo.bar/2"), webClientStub, eventMapperEventTo);
        atomClient.setEventRelay(mock(EventRelay.class));
    }

    @Test
    public void testUpdate() throws Exception {
//        feedRecordCreator.create(7);
//        atomClient.update();
//        verify(atomClient.getEventRelay(), times(7)).sendEventMessage(any(MotechEvent.class));
    }
}
