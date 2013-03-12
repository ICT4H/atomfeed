package org.openmrs.module.feedpublishermodule.mapper;

import org.codehaus.jackson.map.ObjectMapper;
import org.ict4htw.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openmrs.DrugOrder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

@Component
public class DrugOrderSerializer {
    public Event asEvent(DrugOrder drugOrder) throws URISyntaxException, IOException {
        DosageRequest dosageRequest = DosageRequest.create(drugOrder);
        return createEvent(dosageRequest);
    }

    private  Event createEvent(DosageRequest dosageRequest) throws URISyntaxException, IOException {
        String contents = new ObjectMapper().writeValueAsString(dosageRequest);
        String uuid = dosageRequest.uuid;
        String title = "Dosage Request";
        DateTime timeStamp = DateTime.now();
        String uriString = "http://openmrs.org";
        return new Event(uuid,title, timeStamp, uriString,contents);
    }

}
