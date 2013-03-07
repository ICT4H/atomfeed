package org.openmrs.module.feedpublishermodule.mapper;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.DrugOrder;

import java.io.IOException;

public class DosageRequestSerializer {
    public String serialize(DrugOrder drugOrder) throws IOException {
        DosageRequest dosageRequest = DosageRequest.create(drugOrder);
        return new ObjectMapper().writeValueAsString(dosageRequest);
    }
}
