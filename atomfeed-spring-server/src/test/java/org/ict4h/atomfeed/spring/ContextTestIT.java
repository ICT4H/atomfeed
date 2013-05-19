package org.ict4h.atomfeed.spring;

import org.ict4h.atomfeed.server.service.feedgenerator.FeedGenerator;
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
    private FeedGenerator feedGenerator;

    @Test
    public void shouldTestFeedGeneratorCreation(){
        Assert.notNull(feedGenerator);
    }
}
