package org.openmrs.module.feedpublishermodule.mapper;

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;

public class DrugOrderSerializerTest {

    @Test
    public void shouldSerializedListOfDrugOrder() throws IOException, URISyntaxException {
        DrugOrder order = new DrugOrder();
        order.setStartDate(new Date("01/03/2013"));
        order.setFrequency("2/day x 7 days/week");
        order.setDrug(new Drug());
        Assert.assertNotNull(new DrugOrderSerializer().asEvent(order));
    }
}
