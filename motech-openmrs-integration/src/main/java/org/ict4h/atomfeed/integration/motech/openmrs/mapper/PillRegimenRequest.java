package org.ict4h.atomfeed.integration.motech.openmrs.mapper;

import org.motechproject.server.pillreminder.api.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.api.contract.DosageRequest;

import java.util.ArrayList;
import java.util.List;

public class PillRegimenRequest {
    public String externalId;
    public int reminderRepeatIntervalInMinutes;
    public int pillWindowInHours;
    public int bufferOverDosageTimeInMinutes;
    public List<Dosage> dosageRequests;

    public DailyPillRegimenRequest asDailyPillRegimenRequest(){
        List<DosageRequest> dosages = new ArrayList<DosageRequest>();
        for (Dosage request : dosageRequests){
            dosages.add(request.asDosageRequest());
        }
        return new DailyPillRegimenRequest(externalId,pillWindowInHours,reminderRepeatIntervalInMinutes,bufferOverDosageTimeInMinutes,dosages);
    }
}