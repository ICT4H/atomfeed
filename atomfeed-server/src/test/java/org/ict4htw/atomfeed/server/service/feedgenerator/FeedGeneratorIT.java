package org.ict4htw.atomfeed.server.service.feedgenerator;

import static junit.framework.Assert.assertEquals;

import org.ict4htw.atomfeed.SpringIntegrationIT;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FeedGeneratorIT extends SpringIntegrationIT {

    @Autowired
    private FeedGenerator generator;

    @Test
    public void verifyFeedGeneratorBean(){
        assertEquals(generator.getClass(),NumberFeedGenerator.class);
    }
}
