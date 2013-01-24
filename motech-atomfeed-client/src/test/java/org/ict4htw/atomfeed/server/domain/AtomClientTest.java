package org.ict4htw.atomfeed.server.domain;

import com.sun.syndication.feed.atom.Entry;
import org.ict4htw.atomfeed.client.repository.datasource.WebClient;
import org.ict4htw.atomfeed.motechclient.AtomClient;
import org.ict4htw.atomfeed.motechclient.MotechEventMapper;
import org.ict4htw.atomfeed.server.repository.AllEventRecordsStub;
import org.ict4htw.atomfeed.server.repository.InMemoryEventRecordCreator;
import org.ict4htw.atomfeed.server.resource.EventResource;
import org.ict4htw.atomfeed.server.service.EventFeedService;
import org.ict4htw.atomfeed.server.service.EventService;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;

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
        EventFeedService eventFeedService = new EventFeedService(allEventRecords);
        WebClient webClientStub = new WebClientStub(new EventResource(eventFeedService, eventService));
        MotechEventMapper eventMapper=new MotechEventMapper(){
            @Override
            public MotechEvent map(Entry entry) {
                MotechEvent event = new MotechEvent("com.atomfeed.entry."+entry.getId());
                return event;
            }
        };
        atomClient=new AtomClient("http://foo.bar/2", webClientStub, eventMapper);
        atomClient.setEventRelay(mock(EventRelay.class));
    }

    @Test
    public void testUpdate() throws Exception {
        feedRecordCreator.create(7);
        atomClient.update();
        verify(atomClient.getEventRelay(), times(7)).sendEventMessage(any(MotechEvent.class));
    }
}
