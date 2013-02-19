package org.ict4h.atomfeed.integration.motech.openmrs.mapper;

import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.server.pillreminder.api.contract.DailyPillRegimenRequest;

import java.io.IOException;

public class EventMapper {
    public DailyPillRegimenRequest toDailyPillRegimenRequest(String contents) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(contents, PillRegimenRequest.class).asDailyPillRegimenRequest();
    }
}
