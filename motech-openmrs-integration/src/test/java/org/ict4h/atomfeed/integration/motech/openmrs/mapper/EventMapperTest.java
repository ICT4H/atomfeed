package org.ict4h.atomfeed.integration.motech.openmrs.mapper;

import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.motechproject.server.pillreminder.api.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.api.contract.MedicineRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EventMapperTest {

    @Test
    public void shouldMapJsonToDailyPillRegimenRequest() throws IOException {
        String contents = getDosageRequest();
        EventMapper mapper = new EventMapper();
        DailyPillRegimenRequest request = mapper.toDailyPillRegimenRequest(contents);
        assertNotNull(request);
        assertEquals("3842b4fb-f3d4-4088-ac15-eee4ed619098",request.getExternalId());
        List<MedicineRequest> medicineRequests = request.getDosageRequests().get(0).getMedicineRequests();
        assertEquals("Triomune-30", medicineRequests.get(0).getName());
    }

    private String getDosageRequest() throws IOException {
        InputStream stream = this.getClass().getResourceAsStream("/dosageRequest.json");
        StringWriter writer = new StringWriter();
        IOUtils.copy(stream, writer);
        return writer.toString();
    }
}
