package org.ict4h.atomfeed.integration.motech.openmrs.mapper;

import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.api.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.api.contract.DosageRequest;
import org.motechproject.server.pillreminder.api.contract.MedicineRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PillRegimenRequest {
    public String numberOfDaysInAWeek;
    public String numberOfTimesInADay;
    public String drugName;
    public LocalDate startDate;
    public String uuid;

    //Read the following from a configuration as this is Motech specific
    public static final int reminderRepeatIntervalInMinutes = 15;
    public static final int pillWindowInHours = 2;
    public static final int bufferOverDosageTimeInMinutes = 5;
    //The Following are assumed as OpenMRS doesn't specify a start time for the drug regimen
    public static final int startHour = 10;
    public static final int startMinute = 10;

    public DailyPillRegimenRequest asDailyPillRegimenRequest(){
        List<DosageRequest> dosageRequests = new ArrayList<DosageRequest>();
        List<MedicineRequest> medicineRequests = new ArrayList<MedicineRequest>();
        medicineRequests.add(new MedicineRequest(drugName,startDate,null));
        dosageRequests.add(new DosageRequest(startHour,startMinute,medicineRequests));
        return new DailyPillRegimenRequest(uuid,pillWindowInHours,reminderRepeatIntervalInMinutes,bufferOverDosageTimeInMinutes,dosageRequests);
    }
}