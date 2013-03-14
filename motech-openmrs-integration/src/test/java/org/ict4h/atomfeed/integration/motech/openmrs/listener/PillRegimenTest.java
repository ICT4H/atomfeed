package org.ict4h.atomfeed.integration.motech.openmrs.listener;

import org.ict4h.atomfeed.integration.motech.openmrs.mapper.EventMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.motechproject.event.MotechEvent;
import org.motechproject.server.pillreminder.api.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.api.service.PillReminderService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PillRegimenTest {

    private PillReminderService service;
    private EventMapper mapper;

    @Before
    public void setUp(){
        service = Mockito.mock(PillReminderService.class);
        mapper = Mockito.mock(EventMapper.class);
    }

    @Test
    public void shouldCallPillRegimenWithDailyPillRegimenRequestObject() throws IOException {
        MotechEvent event = createMotechEvent();
        DailyPillRegimenRequest request = new DailyPillRegimenRequest(null,0,0,0,null);
        when(mapper.toDailyPillRegimenRequest("foobar")).thenReturn(request);
        new PillRegimen(service, mapper).create(event);
        verify(service).createNew(request);
    }

    private MotechEvent createMotechEvent() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("contents", "foobar");
        return new MotechEvent("",map);
    }
}
