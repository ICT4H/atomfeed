package org.ict4htw.atomfeed.server.service.feedgenerator;

import org.ict4htw.atomfeed.SpringIntegrationIT;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class FeedGeneratorIT extends SpringIntegrationIT {

    @Autowired
    private FeedGenerator generator;

    @Test
    public void verifyFeedGeneratorBean(){
        assertEquals(generator.getClass(),NumberFeedGenerator.class);
    }
}
