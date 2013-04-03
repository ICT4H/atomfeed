package org.ict4h.atomfeed.server.service.feedgenerator;

import junit.framework.Assert;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;
import org.junit.Test;
import org.mockito.Mockito;

public class FeedGeneratorFactoryTest {
    @Test
    public void shouldGetNumberFeedGeneratorByDefault(){
        AllEventRecords eventRecords = Mockito.mock(AllEventRecords.class);
        ChunkingEntries chunkingEntries = Mockito.mock(ChunkingEntries.class);
        FeedGenerator generator = new FeedGeneratorFactory().getFeedGenerator(eventRecords, chunkingEntries);
        Assert.assertEquals(NumberFeedGenerator.class,generator.getClass());
    }
}
