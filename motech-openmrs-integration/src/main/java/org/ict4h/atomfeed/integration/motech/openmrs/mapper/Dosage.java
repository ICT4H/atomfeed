package org.ict4h.atomfeed.integration.motech.openmrs.mapper;

import org.motechproject.server.pillreminder.api.contract.DosageRequest;
import org.motechproject.server.pillreminder.api.contract.MedicineRequest;

import java.util.ArrayList;
import java.util.List;

public class Dosage{
    public int startHour;
    public int startMinute;
    public List<MedicineRegimen> medicineRequests;

    public DosageRequest asDosageRequest() {
        List<MedicineRequest> requests = new ArrayList<MedicineRequest>();
        for(MedicineRegimen regimen : medicineRequests){
            requests.add(regimen.asMedicalRequest());
        }
        return new DosageRequest(startHour,startMinute,requests);
    }
}
