package org.ict4h.atomfeed.client;

import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

@ContextConfiguration("classpath*:*Context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ContextTestIT {

    @Autowired
    private AtomFeedClient atomFeedClient;

    @Test
    public void shouldTestFeedGeneratorCreation(){
        Assert.notNull(atomFeedClient);
    }
}
