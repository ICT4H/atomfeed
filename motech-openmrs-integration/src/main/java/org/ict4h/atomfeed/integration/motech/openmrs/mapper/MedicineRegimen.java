package org.ict4h.atomfeed.integration.motech.openmrs.mapper;

import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.api.contract.MedicineRequest;

public class MedicineRegimen {
    public String name;
    public String startDate;

    public MedicineRequest asMedicalRequest() {
        return new MedicineRequest(name, LocalDate.parse(startDate),null);
    }
}
