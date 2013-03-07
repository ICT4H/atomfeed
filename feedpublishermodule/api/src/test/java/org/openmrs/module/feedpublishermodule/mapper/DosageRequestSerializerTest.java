package org.openmrs.module.feedpublishermodule.mapper;

import junit.framework.Assert;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DosageRequestSerializerTest {

    @Test
    public void shouldSerializedListOfDrugOrder() throws IOException {
        DrugOrder order = new DrugOrder();
        order.setStartDate(new Date("01/03/2013"));
        order.setFrequency("2/day x 7 days/week");
        order.setDrug(new Drug());
        String contents = new DosageRequestSerializer().serialize(order);
        Assert.assertNotNull(contents);
    }
}
