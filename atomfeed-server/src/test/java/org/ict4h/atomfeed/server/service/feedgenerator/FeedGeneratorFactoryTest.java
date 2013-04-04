package org.ict4h.atomfeed.server.service.feedgenerator;

import junit.framework.Assert;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;
import org.ict4h.atomfeed.server.service.helper.ResourceHelper;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

public class FeedGeneratorFactoryTest {
    @Test
    public void shouldGetNumberFeedGeneratorByDefault(){
        AllEventRecords eventRecords = mock(AllEventRecords.class);
        ChunkingEntries chunkingEntries = mock(ChunkingEntries.class);
        FeedGenerator generator = new FeedGeneratorFactory().getFeedGenerator(eventRecords, chunkingEntries, new ResourceHelper());
        Assert.assertEquals(NumberFeedGenerator.class,generator.getClass());
    }

    @Test
    public void shouldGetTimeFeedGenerator(){
        AllEventRecords eventRecords = mock(AllEventRecords.class);
        ChunkingEntries chunkingEntries = mock(ChunkingEntries.class);
        ResourceHelper helper = mock(ResourceHelper.class);
        stub(helper.fetchKeyOrDefault("chunking.strategy","number")).toReturn("time");
        FeedGenerator generator = new FeedGeneratorFactory().getFeedGenerator(eventRecords, chunkingEntries, helper);
        Assert.assertEquals(TimeFeedGenerator.class,generator.getClass());
    }
}
