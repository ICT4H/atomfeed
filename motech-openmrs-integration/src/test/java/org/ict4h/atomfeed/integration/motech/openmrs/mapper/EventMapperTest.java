package org.ict4h.atomfeed.integration.motech.openmrs.mapper;

import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.motechproject.server.pillreminder.api.contract.DailyPillRegimenRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class EventMapperTest {

    @Test
    public void shouldMapJsonToDailyPillRegimenRequest() throws IOException {
        String contents = getDosageRequest();
        EventMapper mapper = new EventMapper();
        DailyPillRegimenRequest request = mapper.toDailyPillRegimenRequest(contents);
        Assert.assertNotNull(request);
    }

    private String getDosageRequest() throws IOException {
        InputStream stream = this.getClass().getResourceAsStream("/dosageRequest.json");
        StringWriter writer = new StringWriter();
        IOUtils.copy(stream, writer);
        return writer.toString();
    }
}
