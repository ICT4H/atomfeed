package org.openmrs.module.feedpublishermodule.mapper;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;


import java.util.Date;
import java.util.UUID;

public class DosageRequestTest {
    @Test
    public void shouldCreateDosageRequestFromDrugOrder(){
        DrugOrder order = new DrugOrder();
        Drug drug = new Drug();
        drug.setName("BluePill");
        order.setDrug(drug);
        order.setFrequency("2/day x 7 days/week");
        Date date = new Date("01/03/2013");
        order.setStartDate(date);
        String uuid = UUID.randomUUID().toString();
        order.setUuid(uuid);

        DosageRequest request = DosageRequest.create(order);
        Assert.assertEquals("BluePill", request.drugName);
        Assert.assertEquals(2,request.numberOfTimesInADay);
        Assert.assertEquals(7,request.numberOfDaysInAWeek);
        Assert.assertEquals(date,request.startDate);
        Assert.assertEquals(uuid,request.uuid);
    }
}
